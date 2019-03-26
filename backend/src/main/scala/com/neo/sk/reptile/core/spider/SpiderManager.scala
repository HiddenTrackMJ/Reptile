package com.neo.sk.reptile.core.spider


import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.neo.sk.reptile.core.proxy.ProxyManager
import com.neo.sk.reptile.core.task.TaskManager
import org.slf4j.LoggerFactory
import com.neo.sk.reptile.core.{spider, task}

/**
  * User: Jason
  * Date: 2019/3/20
  * Time: 16:44
  */

object SpiderManager {

  import com.neo.sk.reptile.common.AppSettings.SpiderConf._

  private val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command

  final case class ChildDead(name:String) extends Command

  case object StartSpider extends Command

  case object StopSpider extends Command

  case class AddTask(task: task.Task) extends Command

  def create():Behavior[Command] = {
    log.debug(s"spider Manager is starting...")
    Behaviors.setup[Command]{
      ctx =>
        if(isWork)
          ctx.self ! StartSpider

        val proxyManager = getProxyManager(ctx)
        val taskManager = getTaskManager(ctx,proxyManager)
        work(taskManager,proxyManager)
    }
  }


  private def work(taskManager:ActorRef[task.TaskCommand],
    proxyManager:ActorRef[ProxyManager.Command],
    isWorking:Boolean = false) : Behavior[Command] ={
    Behaviors.receive[Command]{ (ctx,msg) =>
      msg match {
        case StartSpider =>
          if(!isWorking){
            (1 to spiderNum).foreach(i => getSpider(ctx,taskManager,proxyManager,i))
            work(taskManager,proxyManager,!isWorking)
          }else{
            Behaviors.same
          }
        case StopSpider =>
          if (isWorking) {
            val proxyManager = getProxyManager(ctx)
            (1 to spiderNum).foreach(i => ctx.stop(getSpider(ctx, taskManager, proxyManager,i)))
            work(taskManager,proxyManager,!isWorking)
          } else {
            Behaviors.same
          }

        case AddTask(task) =>
          taskManager ! TaskManager.AddTask(task)
          Behaviors.same

        case ChildDead(name:String)  =>
          log.warn(s"${ctx.self.path} child=$name die ..")
          Behaviors.same

        case other =>
          log.warn(s"${ctx.self.path} recv an unknown,msg=$other")
          Behaviors.ignore
      }
    }
  }



  private def getProxyManager(ctx: ActorContext[Command]):ActorRef[ProxyManager.Command] = {
    val childName = s"proxyManager"
    ctx.child(childName).getOrElse{
      val actor = ctx.spawn(ProxyManager.create(),childName)
      ctx.watchWith(actor,ChildDead(childName))
      actor
    }.upcast[ProxyManager.Command]
  }

  private def getTaskManager(ctx: ActorContext[Command],
    proxyManager: ActorRef[ProxyManager.Command]):ActorRef[task.TaskCommand] = {
    val childName = s"taskManager"
    ctx.child(childName).getOrElse{
      val actor = ctx.spawn(TaskManager.create(proxyManager),childName)
      ctx.watchWith(actor,ChildDead(childName))
      actor
    }.upcast[task.TaskCommand]
  }

  private def getSpider(ctx: ActorContext[Command],
    taskManager: ActorRef[task.TaskCommand],
    proxyManager:ActorRef[ProxyManager.Command],
    index:Int):ActorRef[spider.SpiderCommand] = {
    val childName = s"spider-$index"
    ctx.child(childName).getOrElse{
      val actor = ctx.spawn(Spider.create(taskManager,proxyManager),childName)
      ctx.watchWith(actor,ChildDead(childName))
      actor
    }.upcast[spider.SpiderCommand]
  }



}
