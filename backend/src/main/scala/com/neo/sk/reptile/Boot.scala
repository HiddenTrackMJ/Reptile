package com.neo.sk.reptile

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.dispatch.MessageDispatcher
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.neo.sk.reptile.http.HttpService

import scala.language.postfixOps
import scala.util.{Failure, Success}
import com.neo.sk.reptile.core.SystemActor
import akka.actor.typed.scaladsl.adapter._

/**
  * User: Taoz
  * Date: 11/16/2016
  * Time: 1:00 AM
  */
object Boot extends HttpService {


  import com.neo.sk.reptile.common.AppSettings._
  import concurrent.duration._

  override implicit val system = ActorSystem("reptile", config)
  // the executor should not be the default dispatcher.
  override implicit val executor: MessageDispatcher =
    system.dispatchers.lookup("akka.actor.my-blocking-dispatcher")

  override implicit val materializer = ActorMaterializer()

  override implicit val scheduler = system.scheduler

  override implicit val timeout = Timeout(10 seconds) // for actor asks

  val log: LoggingAdapter = Logging(system, getClass)


  def main(args: Array[String]) {
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
