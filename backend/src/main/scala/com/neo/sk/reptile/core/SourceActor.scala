package com.neo.sk.reptile.core

import akka.actor.typed.scaladsl.{Behaviors, ActorContext, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.reptile.core.spider.SpiderManager
import com.neo.sk.reptile.models.{NewsApp, NewsAppColumn}
import org.slf4j.LoggerFactory

/**
  * User: Jason
  * Date: 2019/3/26
  * Time: 17:27
  */


object SourceActor {


  private val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command

  final case object StartWork extends Command

  final case class ChildDead(name:String) extends Command



  def create(spiderManager: ActorRef[SpiderManager.Command],
    storeActor: ActorRef[StoreActor.Command],
    newsApp: NewsApp) :Behavior[Command] = {
    log.debug(s"NewsAppManager is starting")
    Behaviors.setup[Command]{ ctx =>
      Behaviors.withTimers{ timer =>
        ctx.self ! StartWork
        idle(spiderManager, storeActor, newsApp, timer)
      }
    }
  }

  def idle(spiderManager: ActorRef[SpiderManager.Command],
    storeActor: ActorRef[StoreActor.Command],
    newsApp: NewsApp,
    timer:TimerScheduler[Command]) :Behavior[Command] = {
    Behaviors.receive[Command]{ (ctx,msg) =>

      msg match {
        case StartWork =>
          newsApp.column.foreach(getColumn(ctx,spiderManager,storeActor, newsApp, _, newsApp.useProxy, newsApp.increment))
          Behaviors.same

        case ChildDead(name:String)  =>
          log.warn(s"${ctx.self.path} child=$name die ..")
          Behaviors.same

        case unknown =>
          log.error(s"${ctx.self.path} receive an unknown msg=$unknown")
          Behaviors.same
      }
    }
  }


  private def getColumn(ctx: ActorContext[Command],
    spiderManager: ActorRef[SpiderManager.Command],
    storeActor: ActorRef[StoreActor.Command],
    app: NewsApp,
    newsAppColumn: NewsAppColumn,
    useProxy: Boolean,
    increment:String): ActorRef[Column.Command] = {
    val childName = s"column-${newsAppColumn.name}"
    ctx.child(childName).getOrElse{
      val actor = ctx.spawn(Column.create(spiderManager, storeActor, app.copy(column = Nil), newsAppColumn, useProxy, increment),childName)
      ctx.watchWith(actor,ChildDead(childName))
      actor
    }.upcast[Column.Command]
  }

}
