package com.neo.sk.reptile.core.spider

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import com.neo.sk.reptile.core.task
import com.neo.sk.reptile.Boot.executor
import com.neo.sk.reptile.common.Constant.{SpiderTaskErrorCode, TaskPriority}
import com.neo.sk.reptile.core.proxy.{ProxyInfo, ProxyManager}
import com.neo.sk.reptile.core.task.{FetchTask, Task, TaskCommand, TaskManager}
import com.neo.sk.utils.HttpClientUtil
import org.apache.http.Header
import org.apache.http.client.CookieStore
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.util.{Failure, Success}
import scala.concurrent.duration._

/**
  * User: Jason
  * Date: 2019/3/20
  * Time: 16:45
  */
object Spider {
  import com.neo.sk.reptile.common.AppSettings.SpiderConf._

  private val log = LoggerFactory.getLogger(this.getClass)

  //  sealed trait Command

  private case object TimerKey
  private case object TimeOutMsg extends SpiderCommand

  private case object WorkKey
  private case object WorkTimeOutMsg extends SpiderCommand
  private val workMaxTime = 5.minutes

  final case object NoTask extends SpiderCommand

  final case object StartWork extends SpiderCommand

  final case object FinishTask extends SpiderCommand


  def create(taskManager:ActorRef[task.TaskCommand],
    proxyManager:ActorRef[ProxyManager.Command]
  ):Behavior[SpiderCommand] =
    Behaviors.setup[SpiderCommand]{
      ctx =>
        implicit val stashBuffer: StashBuffer[SpiderCommand] = StashBuffer[SpiderCommand](Int.MaxValue)
        taskManager ! task.FetchTask(ctx.self)
        Behaviors.withTimers[SpiderCommand]{ implicit timer =>
          idle(taskManager, proxyManager)
        }

    }


  def idle(
    taskManager: ActorRef[task.TaskCommand],
    proxyManager:ActorRef[ProxyManager.Command]
  )(
    implicit timer: TimerScheduler[SpiderCommand],
    stashBuffer: StashBuffer[SpiderCommand]):Behavior[SpiderCommand] =

    Behaviors.receive[SpiderCommand] { (ctx, msg) =>
      msg match {
        case r:SpiderTask =>
          timer.startSingleTimer(WorkKey,WorkTimeOutMsg,workMaxTime)
          ctx.self ! StartWork
          work(taskManager, proxyManager, r, Nil)

        case NoTask =>
          timer.startSingleTimer(TimerKey, TimeOutMsg, idleInterval.seconds)
          Behaviors.same

        case TimeOutMsg =>
          taskManager ! task.FetchTask(ctx.self)
          Behaviors.same

        case x =>
          log.warn(s"${ctx.self.path} unknown msg: $x")
          Behaviors.same
      }
    }


  def work(taskManager: ActorRef[TaskCommand],
    proxyManager:ActorRef[ProxyManager.Command],
    task:SpiderTask,
    cmdStash:List[SpiderCommand])(
    implicit timer: TimerScheduler[SpiderCommand],
    stashBuffer: StashBuffer[SpiderCommand]):Behavior[SpiderCommand] = {
    Behaviors.receive[SpiderCommand] { (ctx, msg) =>
      msg match {
        case StartWork =>
          log.debug(s"${ctx.self.path} is work for task url=${task.url}")
          val fetchFuture = task.taskType match {
//            case TaskType.image => fetch(task.url)
            case TaskType.articlePage => fetch(task.url,proxyOption = task.proxyOption,cookieStore = task.cookieStoreOption,task.headerOpt,task.code)
            case TaskType.columnPage => fetch(task.url,proxyOption = task.proxyOption,cookieStore = task.cookieStoreOption,task.headerOpt,task.code)
          }
          fetchFuture.onComplete{
            case Success(rst) =>
              task.replyTo ! SpiderRst(task,rst)
              task.proxyOption.foreach(addValidProxy(proxyManager, _))
              ctx.self ! FinishTask

            case Failure(error) =>
              log.warn(s"${ctx.self.path} exec task=$task failed,error+${error.getMessage}")
              task.proxyOption.foreach(deleteValidProxy(proxyManager, _))
              handleRetryTaskByProxy(task,taskManager,error)
              //              task.replyTo ! SpiderRst(task,Left(SpiderTaskError(error.getMessage,code = -1)))
              ctx.self ! FinishTask
          }

          Behaviors.same


        case FinishTask =>
          timer.cancel(WorkKey)
          cmdStash.reverse.foreach(ctx.self ! _)
          taskManager ! FetchTask(ctx.self)
          idle(taskManager,proxyManager)


        case WorkTimeOutMsg =>
          log.warn(s"${ctx.self.path} exec task=$task time out")
          cmdStash.reverse.foreach(ctx.self ! _)
          taskManager ! FetchTask(ctx.self)
          idle(taskManager, proxyManager)

        case _ =>
          work(taskManager, proxyManager,task, msg :: cmdStash)
      }

    }
  }

  private def fetch(url: String,
    proxyOption: Option[ProxyInfo],
    cookieStore: Option[CookieStore] = None,
    headerOpt:Option[List[Header]] = None,
    code:String="utf-8"
  ) = {
    HttpClientUtil.fetch(url, proxyOption,headersOp = headerOpt, cookieStore = cookieStore,code)
  }

//  private def fetch(imageUrl:String) = {
//    HestiaClient.uploadImgByUrl(imageUrl).map{
//      case Right(url) =>
//        Right(SpiderTaskSuccess(url))
//      case Left(e) =>
//        Left(SpiderTaskError(e,SpiderTaskErrorCode.hestiaImageUploadError))
//    }
//  }

  private def handleRetryTaskByProxy(
    task:SpiderTask,
    taskManager: ActorRef[TaskCommand],
    error:Throwable
  ): Unit = {
    if(task.tryTime >= task.maxTryTime){
      val needProxy = if(task.proxyOption.isDefined) true else false
      val priority = TaskPriority.genTaskPriorityByType(task.taskType)
      taskManager ! TaskManager.AddTask(Task(task.copy(proxyOption = None),priority,needProxy))
    }else{
      //retry
      task.replyTo ! SpiderRst(task,Left(SpiderTaskError(error.getMessage,code = -1)))
    }
  }

  private def addValidProxy(proxyManager: ActorRef[ProxyManager.Command], p: ProxyInfo): Unit = {
    proxyManager ! ProxyManager.AddValidProxy(p)
  }

  private def deleteValidProxy(proxyManager: ActorRef[ProxyManager.Command], p: ProxyInfo): Unit = {
    proxyManager ! ProxyManager.RemoveProxy(p)
  }

}
