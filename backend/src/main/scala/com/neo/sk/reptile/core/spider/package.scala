package com.neo.sk.reptile.core


import akka.actor.typed.ActorRef
import org.apache.http.Header
import org.apache.http.client.CookieStore
import org.apache.http.message.BasicHeader
import com.neo.sk.reptile.core.proxy._


/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 11:43
  */
package object spider {

  trait SpiderCommand
  case class SpiderTask(
    url:String,
    commentUrl: Option[String] = None,
    taskType:TaskType.Value,
    cookieStoreOption: Option[CookieStore],
    proxyOption:Option[ProxyInfo],
    replyTo: ActorRef[SpiderRst],
    tryTime: Int = 0,
    maxTryTime: Int = 5,
    code:String = "utf-8",
    headerOpt:Option[List[Header]] = None
  ) extends SpiderCommand

  object TaskType extends Enumeration{
    val columnPage, articlePage, image , comment, firstComment= Value
  }

  case class SpiderTaskSuccess(entity:String,code:Int = 200,url: String, commentUrl: Option[String] = None)
  case class SpiderTaskError(entity:String,code:Int)
  final case class SpiderRst(task: SpiderTask, rst: Either[SpiderTaskError, SpiderTaskSuccess])

  object spiderHeader{
    private val sinaHttpHeaders = List[Header](
      new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
      new BasicHeader("Accept-Encoding", "gzip, deflate"),
      new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"),
      new BasicHeader("Cache-Control", "max-age=0"),
      new BasicHeader("Connection", "keep-alive"),
      //    new BasicHeader("Host", "m.byr.cn"),
      new BasicHeader("Upgrade-Insecure-Requests", "1"),
      new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
    )

    private val chinaNewsHttpHeaders = List[Header](
      new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
      new BasicHeader("Accept-Encoding", "gzip, deflate"),
      new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"),
      new BasicHeader("Cache-Control", "max-age=0"),
      new BasicHeader("Connection", "keep-alive"),
      //    new BasicHeader("Host", "m.byr.cn"),
      new BasicHeader("Upgrade-Insecure-Requests", "1"),
      new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
    )

    private val netEaseHttpHeaders = List[Header](
      new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
      new BasicHeader("Accept-Encoding", "gzip, deflate"),
      new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"),
      new BasicHeader("Cache-Control", "max-age=0"),
      new BasicHeader("Connection", "keep-alive"),
      //    new BasicHeader("Host", "m.byr.cn"),
      new BasicHeader("Upgrade-Insecure-Requests", "1"),
      new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
    )


    def buildHeader(appName:String): Option[List[Header]] ={
      appName match {
        case "sina" => Some(sinaHttpHeaders)
        case "chinaNews" => Some(chinaNewsHttpHeaders)
        case "netEase" => Some(netEaseHttpHeaders)
        case _ => None
      }
    }
  }





}