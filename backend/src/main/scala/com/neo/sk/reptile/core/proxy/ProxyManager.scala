package com.neo.sk.reptile.core.proxy

/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 15:51
  */

import akka.actor.ActorPath
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import org.slf4j.LoggerFactory
import com.neo.sk.reptile.Boot.executor
import com.neo.sk.reptile.Boot.scheduler
import com.neo.sk.reptile.core.spider.{Spider, SpiderCommand, SpiderTask}
import com.neo.sk.utils.ProxyClient

import scala.collection.mutable
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._
object ProxyManager {
  import com.neo.sk.reptile.common.AppSettings.proxyConf._

  private val log = LoggerFactory.getLogger(this.getClass)

  private val proxyFetchInterval = 5.minutes

  private val proxyFetchTimeKey = "proxy_fetch_key"

  private def getContext(context:ActorContext[Command]) = (context.self,context.self.path)



  sealed trait Command

  final case object FetchProxy extends Command

  final case class AddProxyList(proxyList:List[ProxyInfo]) extends Command

  final case class GetProxy(replyTo:ActorRef[ProxyRsp]) extends Command

  final case class AddValidProxy(proxy: ProxyInfo) extends Command

  final case class RemoveProxy(proxy:ProxyInfo) extends Command

  final case class GetProxy4Spider(task:SpiderTask,spiderRef:ActorRef[SpiderCommand]) extends Command

  def create():Behavior[Command] = {
    Behaviors.setup[Command]{
      ctx =>
        val (selfRef,path) = getContext(ctx)
        log.debug(s"$path proxyManager is starting")
        if(isWork){
          selfRef ! FetchProxy
        }
        Behaviors.withTimers[Command]{
          timer =>
            work(timer,mutable.Queue[ProxyInfo](),Nil)
        }
    }
  }

  def work(
    timer:TimerScheduler[Command],
    noUseProxyQueue:mutable.Queue[ProxyInfo],
    validProxyList:List[ProxyInfo]
  ) : Behavior[Command] = {
    Behaviors.receive[Command]{
      (ctx, msg) =>
        val (selfRef,path) = getContext(ctx)
        //        log.debug(s"${path} proxyManager is working..")

        msg match {
          case FetchProxy =>
            log.debug(s"$path receive a msg:$msg")
            getProxy(timer,selfRef,path)
            //            timer.startSingleTimer(proxyFetchTimeKey,FetchProxy,proxyFetchInterval)
            Behaviors.same

          case AddProxyList(proxyList)=>
            //            log.debug(s"${path} receive a msg:${msg}")
            proxyList.foreach(noUseProxyQueue.enqueue(_))
            log.debug(s"$path get Add proxy list num is ${proxyList.length},and now noUseProxyQueue num is ${noUseProxyQueue.length}")
            Behaviors.same

          case GetProxy(replyTo: ActorRef[ProxyRsp]) =>
            if(validProxyList.nonEmpty){
              val random = new Random(System.currentTimeMillis()).nextInt(validProxyList.length)
              replyTo ! ProxyRsp(Some(validProxyList(random)))
            }else if(noUseProxyQueue.nonEmpty){
              replyTo ! ProxyRsp(Some(noUseProxyQueue.dequeue()))
            }else{
              if (isWork) {
                ctx.self ! FetchProxy
                scheduler.scheduleOnce(5.seconds){
                  selfRef ! msg
                }
              } else {
                replyTo ! ProxyRsp(None)
              }
            }
            Behaviors.same

          case GetProxy4Spider(task:SpiderTask,spiderRef: ActorRef[SpiderCommand]) =>
            if(validProxyList.nonEmpty){
              val random = new Random(System.currentTimeMillis()).nextInt(validProxyList.length)
              spiderRef ! task.copy(proxyOption = Some(validProxyList(random)))
            }else if(noUseProxyQueue.nonEmpty){
              spiderRef ! task.copy(proxyOption = Some(noUseProxyQueue.dequeue()))
            }else{
              if (isWork) {
                ctx.self ! FetchProxy
                scheduler.scheduleOnce(5.seconds){
                  selfRef ! msg
                }
              } else {
                spiderRef ! task
              }
            }
            Behaviors.same



          case AddValidProxy(proxy) =>
            log.debug(s"$path receive a msg:$msg and now vaild proxy length=${validProxyList.length}")
            if(validProxyList.contains(proxy)){
              Behaviors.same
            }else{
              work(timer,noUseProxyQueue, proxy :: validProxyList)
            }

          case RemoveProxy(proxy) =>
            log.debug(s"$path receive a msg:$msg and now noUseProxy'size=${noUseProxyQueue.filterNot(_ == proxy).size},valid size=${validProxyList.filterNot(_ == proxy).size}")
            work(timer,noUseProxyQueue.filterNot(_ == proxy),validProxyList.filterNot(_ == proxy))

          case other =>
            Behaviors.unhandled
        }
    }
  }

  private def getProxy(
    timer:TimerScheduler[Command],
    selfRef:ActorRef[Command],
    path:ActorPath
  ): Unit = {
    ProxyClient.getProxy(proxyFetchUrl).onComplete{
      case Success(rsp) =>
        rsp match {
          case Right(proxyList) =>
            selfRef ! AddProxyList(proxyList)
          case Left(error) =>
            log.debug(s"$path fetch proxy error:$error")
        }
      case Failure(error) =>
        log.debug(s"$path fetch proxy error:${error.getMessage}")
    }
  }

}
