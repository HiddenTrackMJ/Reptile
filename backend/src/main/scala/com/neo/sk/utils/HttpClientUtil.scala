package com.neo.sk.utils

import java.io.IOException
import java.nio.charset.CodingErrorAction


import org.apache.http.{Consts, Header, HttpHost, HttpStatus}
import org.apache.http.client.{CookieStore, HttpRequestRetryHandler}
import org.apache.http.client.config.{CookieSpecs, RequestConfig}
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.config.{ConnectionConfig, MessageConstraints}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeader
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.collection.JavaConverters._
import scala.util.{Try, Success, Failure}

import com.neo.sk.reptile.core.proxy._
import com.neo.sk.reptile.core.spider._
import com.neo.sk.reptile.Boot.executor

/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 12:42
  */
object HttpClientUtil {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val httpHeaders = List[Header](
    new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
    new BasicHeader("Accept-Encoding", "gzip, deflate"),
    new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6"),
    new BasicHeader("Cache-Control", "max-age=0"),
    new BasicHeader("Connection", "keep-alive"),
    //    new BasicHeader("Host", "m.byr.cn"),
    new BasicHeader("Upgrade-Insecure-Requests", "1"),
    new BasicHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36")
  )


  private val globalConfig = RequestConfig.custom()
    //    .setCookieSpec(CookieSpecs.BEST_MATCH)
    .setCircularRedirectsAllowed(false)
    .setRedirectsEnabled(false)
    .setConnectTimeout(10000)
    .setSocketTimeout(10000)
    //        .setConnectionRequestTimeout(10000)
    .build()

  private val retryHeader = new HttpRequestRetryHandler() {
    /*override def retryRequest(exception: IOException, executionCount: Int, context: HttpContext): Boolean = {
      if (executionCount >= 2) { // Do not retry if over max retry count
        return false
      }
      if (exception.isInstanceOf[InterruptedIOException]) { // Timeout
        return false
      }
      if (exception.isInstanceOf[UnknownHostException]) { // Unknown host
        return false
      }
      if (exception.isInstanceOf[ConnectTimeoutException]) { // Connection refused
        return false
      }
      if (exception.isInstanceOf[SSLException]) { // SSL handshake exception
        return false
      }
      val clientContext = HttpClientContext.adapt(context)
      val request = clientContext.getRequest
      val idempotent = !request.isInstanceOf[HttpEntityEnclosingRequest]
      if (idempotent) { // Retry if the request is considered idempotent
        return true
      }
      false
    }*/
    override def retryRequest(exception: IOException, executionCount: Int, context: HttpContext): Boolean = false
  }

  private val threadNum = 100

  val messageConstraints: MessageConstraints =
    MessageConstraints.
      custom.setMaxHeaderCount(200).
      setMaxLineLength(5000).build

  val connectionConfig: ConnectionConfig = ConnectionConfig.custom.
    setMalformedInputAction(CodingErrorAction.IGNORE).
    setUnmappableInputAction(CodingErrorAction.IGNORE).
    setCharset(Consts.UTF_8).setBufferSize(64 * 1024).
    setMessageConstraints(messageConstraints).build

  val cm = new PoolingHttpClientConnectionManager
  cm.setMaxTotal(threadNum + 5)
  cm.setDefaultMaxPerRoute(threadNum + 5)
  cm.setDefaultConnectionConfig(connectionConfig)

  lazy val httpClient: CloseableHttpClient =
    HttpClientBuilder.create()
      .setConnectionManager(cm)
      .setDefaultHeaders(httpHeaders.asJava)
      .setRetryHandler(retryHeader)
      .setDefaultRequestConfig(globalConfig).build()


  def fetch(url: String,
    proxyOption: Option[ProxyInfo],
    headersOp: Option[List[Header]] = None,
    cookieStore: Option[CookieStore] = None,
    code:String = "UTF-8"
  ): Future[Either[SpiderTaskError, SpiderTaskSuccess]] = {
    Future {
      try {
        val request = new HttpGet(url)

        headersOp.foreach(h => request.setHeaders(h.toArray))

        val clientContext = new HttpClientContext()


        cookieStore.foreach { c =>
          clientContext.setCookieStore(c)
        }

        //set proxy
        if (proxyOption.nonEmpty) {
          val proxy = proxyOption.get

          val httpHost = new HttpHost(proxy.ip, proxy.port.toInt)
          val config = RequestConfig.custom()
            .setProxy(httpHost)
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setRedirectsEnabled(false)
            .setCircularRedirectsAllowed(false)
            .setCookieSpec(CookieSpecs.DEFAULT)
            .build()
          request.setConfig(config)
        }

        val response = httpClient.execute(request, clientContext)
        val statusCode = response.getStatusLine.getStatusCode
        val entity = response.getEntity
        val str = EntityUtils.toString(entity, code)//EntityUtils.toString(entity, "utf-8")
        EntityUtils.consume(response.getEntity)
        response.close()

        if (statusCode == HttpStatus.SC_OK) {
          Right(SpiderTaskSuccess(str))
        } else {
          Left(SpiderTaskError(str,statusCode))
        }
      } catch {
        case e: Exception =>
          log.debug(s"fetch url:$url error: $e")
          //返回
          throw e
        //          Left(SpiderTaskError(s"${e.getMessage}",code = -1))
      }
    }
  }

  def fetchImg[A](
    url: String,
    proxyOption: Option[String],
    headersOp: Option[Array[Header]] = None,
    cookieStore: Option[CookieStore] = None
  ): Future[Either[String, (Array[Byte], String)]] = {
    Future {
      try {
        val request = new HttpGet(url)

        headersOp.foreach(h => request.setHeaders(h))

        val clientContext = new HttpClientContext()

        cookieStore.foreach { c =>
          //                  log.debug(c.getCookies.toString)
          clientContext.setCookieStore(c)
        }

        //set proxy
        if (proxyOption.nonEmpty) {
          val proxy = proxyOption.get.split(":")

          val httpHost = new HttpHost(proxy(0), proxy(1).toInt)
          val config = RequestConfig.custom()
            .setProxy(httpHost)
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setRedirectsEnabled(false)
            .setCircularRedirectsAllowed(false)
            .setCookieSpec(CookieSpecs.DEFAULT)
            .build()
          request.setConfig(config)
        }

        val response = httpClient.execute(request, clientContext)
        val statusCode = response.getStatusLine.getStatusCode
        log.debug(s"img: $url, response: ${response.getAllHeaders.toList}")
        val entity = response.getEntity
        val str = EntityUtils.toByteArray(entity) //EntityUtils.toString(entity, "utf-8")
        EntityUtils.consume(response.getEntity)
        response.close()

        if (statusCode == HttpStatus.SC_OK) {
          val contentType = response.getHeaders("Content-Type")
          val fileType = contentType(0).toString.split("/").last
          Right((str, fileType))
        } else {
          log.debug(s"not ok error: url: $url str: $str")
          Left("not ok")
        }
      } catch {
        case e: Exception =>
          log.debug(s"fetch url:$url error: $e")
          Left(e.toString)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    var ipList = List.empty[ProxyInfo]
    val allRegex = """<td>\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3}</td>\n* *<td>\d{1,5}</td>""".r
    val ipRegex = """(\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3})""".r
    val hostRegex = """(\d{1,5})""".r
    val url = "https://www.163.com/"
    println(s"start")
    fetch("https://www.xicidaili.com/nn/", None, None, None).onComplete{ a =>
      if (a.isSuccess){
        a.get match {
          case Right(entity) => val content = entity.entity.toString.replaceAll("\\n","").replaceAll(" ","")
            allRegex.findAllIn(content).toList.foreach{ p =>
              if (ipRegex.findFirstIn(p).isDefined && hostRegex.findFirstIn(p).isDefined){
                ipList = ipList :+ ProxyInfo(ipRegex.findFirstIn(p).get,hostRegex.findFirstIn(p).get)
              }
            }
            ipList.foreach{ i =>
              fetch(url, Some(i), None, None).onComplete{ q =>
                if (q.isSuccess){
                  q.get match {
                    case Right(r) =>
                      println(s"yes: ${r.code} ${r.entity}")
                    case Left(e) => println(s"error2: $e")
                  }
                }
              }
            }
//            println(ipList.length,ipList)
          case Left(e) => println(s"error: $e")
        }
      }
    }




//    Thread.sleep(1000)

    println(s"end")
  }
}
