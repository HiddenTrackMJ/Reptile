package com.neo.sk.reptile.http

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.model.headers.{CacheDirective, `Cache-Control`}
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.mapResponseHeaders
import akka.stream.Materializer
import akka.util.Timeout
import com.neo.sk.utils.CirceSupport

import scala.concurrent.ExecutionContextExecutor

/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 13:43
  */
trait BaseService extends CirceSupport with ServiceUtils{

  def addCacheControlHeaders(first: CacheDirective, more: CacheDirective*): Directive0 = {
    mapResponseHeaders { headers =>
      `Cache-Control`(first, more: _*) +: headers
    }
  }

  implicit val system: ActorSystem

  implicit val executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler

}