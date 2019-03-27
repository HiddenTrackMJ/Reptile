package com.neo.sk.reptile.core


import akka.actor.typed.scaladsl.{Behaviors, ActorContext, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.reptile.core.spider.SpiderManager
import com.neo.sk.reptile.models.NewsApp
import org.slf4j.LoggerFactory

import scala.collection.mutable


/**
  * User: Jason
  * Date: 2019/3/26
  * Time: 17:27
  */


object SourceManager {
  import com.neo.sk.reptile.common.AppSettings.NewsAppConf._

  private val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command

  final case object StartWork extends Command

  final case class ChildDead(name:String) extends Command

  def create(spiderManager:ActorRef[SpiderManager.Command],
    storeActor: ActorRef[StoreActor.Command]) :Behavior[Command] = {
    log.debug(s"SourceManager is starting")
    Behaviors.setup[Command]{ ctx =>
      Behaviors.withTimers{ timer =>
        init()
        log.debug(s"newsApp=${newsAppMap.size}")
        ctx.self ! StartWork
        idle(spiderManager, newsAppMap, storeActor, timer)
      }
    }
  }

  def idle(spiderManager:ActorRef[SpiderManager.Command],
    newsAppHashMap:mutable.HashMap[Int,NewsApp],
    storeActor:ActorRef[StoreActor.Command],
    timer:TimerScheduler[Command]) :Behavior[Command] = {
    Behaviors.receive[Command]{ (ctx,msg) =>

      msg match {
        case StartWork =>
          newsAppHashMap.values.foreach(getNewsApp(ctx, spiderManager, storeActor, _))

          Behaviors.same

        case ChildDead(name:String)  =>
          log.warn(s"${ctx.self.path} child=$name die ..")
          Behaviors.same

        case unknown =>
          log.error(s"${ctx.self.path} recv an unknown msg=$unknown")
          Behaviors.ignore
      }
    }
  }


  private def getNewsApp(ctx: ActorContext[Command],
    spiderManager: ActorRef[SpiderManager.Command],
    storeActor: ActorRef[StoreActor.Command],
    newsApp: NewsApp): ActorRef[SourceActor.Command] = {
    val childName = s"SourceActor-${newsApp.name}"
    ctx.child(childName).getOrElse{
      val actor = ctx.spawn(SourceActor.create(spiderManager, storeActor, newsApp),childName)
      ctx.watchWith(actor,ChildDead(childName))
      actor
    }.upcast[SourceActor.Command]
  }


}
