package com.neo.sk.reptile

import akka.actor.typed.ActorRef
import akka.actor.{ActorSystem, Scheduler}
import com.neo.sk.reptile.core.spider.SpiderManager
import akka.actor.typed.scaladsl.adapter._
import akka.dispatch.MessageDispatcher
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.neo.sk.reptile.core.StoreActor
import com.neo.sk.reptile.http.HttpService

import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * User: Jason
  * Date: 2019/3/15
  * Time: 17:23
  */



object Boot extends HttpService {


  import com.neo.sk.reptile.common.AppSettings._
  import scala.concurrent.duration._

  override implicit val system: ActorSystem = ActorSystem("reptile", config)
  // the executor should not be the default dispatcher.
  override implicit val executor: MessageDispatcher =
    system.dispatchers.lookup("akka.actor.my-blocking-dispatcher")

  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  override implicit val scheduler: Scheduler = system.scheduler

  override implicit val timeout: Timeout = Timeout(10 seconds) // for actor asks

  val log: LoggingAdapter = Logging(system, getClass)

  val spiderManager: ActorRef[SpiderManager.Command] = system.spawn(SpiderManager.create(),"spiderManager")

  val storeActor: ActorRef[StoreActor.Command] = system.actorOf(RoundRobinPool(3).props(PropsAdapter(StoreActor.work)), "StoreActor")

  def main(args: Array[String]): Unit = {
    log.info("Starting.")
    val binding = Http().bindAndHandle(routes, httpInterface, httpPort)
    binding.onComplete {
      case Success(b) ⇒
        val localAddress = b.localAddress
        println(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
      case Failure(e) ⇒
        println(s"Binding failed with ${e.getMessage}")
        system.terminate()
        System.exit(-1)
    }
  }



}
