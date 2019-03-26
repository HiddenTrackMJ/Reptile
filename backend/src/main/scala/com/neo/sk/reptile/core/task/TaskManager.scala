package com.neo.sk.reptile.core.task

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{Behaviors, StashBuffer, TimerScheduler}
import com.neo.sk.reptile.core.proxy.ProxyManager
import com.neo.sk.reptile.core.spider.{Spider, TaskType}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.duration._

/**
  * User: Jason
  * Date: 2019/3/20
  * Time: 16:45
  */
object TaskManager {

  private val log = LoggerFactory.getLogger(this.getClass)

  private case object TimerKey

  private case object TimeoutMsg extends TaskCommand

  case class AddTask(task:Task) extends TaskCommand

  private val taskSpeedReportTime = 1.minutes

  implicit val ord:Ordering[Task] = Ordering.by(_.priority)

  private var count = 0



  def create(proxyManager:ActorRef[ProxyManager.Command]):Behavior[TaskCommand] = {
    log.debug(s"taskManager is starting")
    Behaviors.setup[TaskCommand]{ ctx =>
      implicit val stashBuffer: StashBuffer[TaskCommand] = StashBuffer[TaskCommand](Int.MaxValue)
      Behaviors.withTimers[TaskCommand]{ implicit timer =>
        timer.startSingleTimer(TimerKey,TimeoutMsg,taskSpeedReportTime)
        idle(proxyManager,mutable.PriorityQueue[Task]())
      }
    }
  }

  private def idle(
    proxyManager:ActorRef[ProxyManager.Command],
    taskQueue:mutable.PriorityQueue[Task]
  )(
    implicit timer: TimerScheduler[TaskCommand],
    stashBuffer: StashBuffer[TaskCommand]):Behavior[TaskCommand] = {
    Behaviors.receive[TaskCommand]{ (ctx,msg) =>
      msg match {
        case AddTask(task) =>
          taskQueue.enqueue(task)
          Behaviors.same

        case FetchTask(spiderRef) =>
          if(taskQueue.isEmpty){
            spiderRef ! Spider.NoTask
            Behaviors.same
          }else{
            count += 1
            val task = taskQueue.dequeue()
            if(task.needProxy && task.task.taskType != TaskType.image){
              proxyManager ! ProxyManager.GetProxy4Spider(task.task,spiderRef)
            }else{
              spiderRef ! task.task
            }
            Behaviors.same
          }

        case TimeoutMsg =>
          log.debug(s"${ctx.self.path} current have ${taskQueue.length} tasks which need to handle,cur speed = $count/min")
          count = 0
          timer.startSingleTimer(TimerKey,TimeoutMsg,taskSpeedReportTime)
          Behaviors.same


        case unknown =>
          log.warn(s"${ctx.self.path} receive an unknown msg:$msg")
          Behaviors.ignore
      }
    }
  }
}