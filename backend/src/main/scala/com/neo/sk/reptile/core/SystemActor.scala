package com.neo.sk.reptile.core

import java.util.Calendar

import scala.concurrent.duration._
import org.slf4j.LoggerFactory
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.reptile.models.dao.UserDAO
import com.neo.sk.reptile.Boot.executor
import scala.util.{Failure, Success}


/**
  * Created by dry on 2018/5/6.
  */
object SystemActor {

  private val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command

  case object CleanCoins extends Command

  case object RechargeCoins extends Command

  case class RetryRechargeCoins(id: Long, coin: Double, coinStandard: Int, retryTime: Int) extends Command

  private final val RetryTime = 3

  private final case object CleanCoinsKey
  private final case object RechargeCoinsKey
  private final case object RetryRechargeCoinsKey

  private[this] def delay() = { //周日19点50点
    val cal = Calendar.getInstance
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val zero = if (System.currentTimeMillis() < cal.getTime.getTime) {
      cal.getTime.getTime - System.currentTimeMillis()
    } else {
      (cal.getTime.getTime + 7 * 24 * 60 * 60 * 1000) - System.currentTimeMillis()
    }
    zero + 19 * 60 * 60 * 1000 + 50 * 60 * 1000 //19点50
  }

  private[this] def delay1() = { //周日19点55分
    delay() + 5 * 60 * 1000
  }

  val behavior = init()

  def init(): Behavior[Command] = {
    Behaviors.setup[Command] { ctx =>
      Behaviors.withTimers[Command] { implicit timer =>
        timer.startSingleTimer(CleanCoinsKey, CleanCoins, delay().millis)
        timer.startSingleTimer(RechargeCoinsKey, RechargeCoins, delay1().millis)
        idle()
      }
    }
  }

  def idle()(implicit timer: TimerScheduler[Command]): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case CleanCoins =>
          timer.startSingleTimer(CleanCoinsKey, CleanCoins, 7.days)
          UserDAO.getCleanUser().onComplete{
            case Success(users) =>
              users.foreach { u =>
                UserDAO.cleanUserCoin(u.id, u.coin)
              }

            case Failure(e) =>
              log.debug(s"cleanUserCoin error....$e")
              timer.startSingleTimer(CleanCoinsKey + "retry", CleanCoins, 5.seconds)
          }
          Behaviors.same

        case RechargeCoins =>
          timer.startSingleTimer(RechargeCoinsKey, RechargeCoins, 7.days)
          UserDAO.getAllUser().onComplete{
            case Success(users) =>
              users.foreach{u =>
                UserDAO.updateUserCoin(u.id, u.coin, u.coinStandard).onComplete {
                  case Success(s) =>
                    log.info(s"charge ${u.id} success")

                  case Failure(e) =>
                    log.debug(s"charge ${u.id} error......$e")
                    timer.startSingleTimer(RetryRechargeCoinsKey + u.id.toString, RetryRechargeCoins(u.id, u.coin, u.coinStandard, RetryTime), 5.seconds)
                }
              }

            case Failure(e) =>
              log.debug(s"cleanUserCoin error....$e")
              timer.startSingleTimer(RechargeCoinsKey, RechargeCoins, 5.seconds)
          }
          Behaviors.same

        case RetryRechargeCoins(id, coin, coinStandard, retryTime) =>
          UserDAO.updateUserCoin(id, coin, coinStandard).onComplete {
            case Success(s) =>
              log.info(s"charge $id success")

            case Failure(e) =>
              log.debug(s"charge $id error......$e")
              if(retryTime > 1)
                timer.startSingleTimer(RetryRechargeCoinsKey + id.toString, RetryRechargeCoins(id, coin, coinStandard, retryTime - 1), 5.seconds)
              else
                log.error(s"charge $id error for 3 times......$e")
          }
          Behavior.same

        case x =>
          log.warn(s"${ctx.self.path} unknown msg: $x")
          Behaviors.unhandled
      }
    }
  }

}
