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
import com.neo.sk.reptile.core.proxy._
import com.neo.sk.reptile.core.spider._
import com.neo.sk.reptile.Boot.executor
import com.neo.sk.reptile.common.Constant.TaskPriority
import com.neo.sk.reptile.core.{spider, task}
import com.neo.sk.reptile.core.task.Task
import io.circe.parser.decode
import io.circe.parser.parse
import io.circe.Decoder
import io.circe.generic.auto._
import org.jsoup.Jsoup

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
    proxyOption: Option[ProxyInfo] = None,
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
          Right(SpiderTaskSuccess(str,statusCode,url))
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

  private val ntesColumnUrlFirstRegex = "http://([.a-zA-Z0-9]+)/special/([0-9A-Za-z]+)/([a-zA-Z_]+).js\\?callback=data_callback".r
  private val ntesColumnUrlRegex = "http://([.a-zA-Z0-9]+)/special/([0-9A-Za-z]+)/([a-zA-Z_]+)_(\\d*).js\\?callback=data_callback".r
  private def parseColumnUrl(url:String):Either[SpiderTaskError,(String,String,String,Int)] = {
    try {
      url match {

        case ntesColumnUrlFirstRegex(domain,ids,column) =>
          Right((domain,ids,column,1))
        case ntesColumnUrlRegex(domain,ids,column,page) =>
          Right((domain,ids,column,page.toInt))

        case unknow =>
          Left(SpiderTaskError(s"url=$url match failed",-4))
      }
    }catch {
      case e:Exception =>
        Left(SpiderTaskError(s"parser column url failed,error:${e.getMessage}",-5))
    }

  }

  private def transEntity2Json(entity:String):String = {
    val first = entity.indexOf("(")
    val end = entity.lastIndexOf(")")
    //    println(first,end,entity.size)
    val x = entity.substring(first+1,end)
    x
  }

  private def transImageList2Json(entity:String):String = {
    val first = entity.indexOf("[")
    val end = entity.lastIndexOf("]")
    //    println(first,end,entity.size)
    val x = entity.substring(first,end+1)
    x
  }

  private def isArticleUrl(url:String):Boolean = {
    if(url.contains("cai.163.com") || url.contains("v.163.com") || url.contains("dy.163.com/v2")){
      false
    }else{
      true
    }

  }

  private def getDate(s:String) = {
    val re = "(\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)(.*)".r
    s match {
      case re(y,m,d,h,mm,ss,_) =>
        s"${y}年${m}月${d}日 ${h}:${mm}:$ss"

      case _ =>
        ""
    }
  }

  case class NtesArticleElem(docurl:String,newstype:String,label: String,time:String)

  private def parseColumnPage(domain:String,
    ids:String,
    column:String,
    currentPage:Int,
    entity:String
  ) : Either[spider.SpiderTaskError, List[NtesArticleElem]] = {
    try{
      val jsonStr = transEntity2Json(entity)
      decode[List[NtesArticleElem]](jsonStr) match {
        case Right(rsp) =>
            Right(rsp)
        case Left(error) =>
          Left(SpiderTaskError(s"parse json error=$error",code = -3))
      }

    }catch {
      case e: Exception =>
        log.debug(s"parse column page failed, error=${e.getMessage}")
        Left(SpiderTaskError(s"parse column page failed, error=${e.getMessage}",-2))
    }
  }

  def parseColumn(rst: Either[SpiderTaskError, SpiderTaskSuccess]): Either[spider.SpiderTaskError, List[NtesArticleElem]] = {
    rst match {
      case Right(r:SpiderTaskSuccess) =>
        if(r.code == 200){

          parseColumnUrl(r.url) match {
            case Right((domain,ids,column,page)) =>
              parseColumnPage(domain,ids,column,page,r.entity)
            case Left(t) =>
              Left(t)
          }

        }else{
          Left(SpiderTaskError(s"response code=${r.code} is not valid",code = r.code))
        }

      case Left(r:SpiderTaskError) =>
        Left(r)
    }
  }

  def CrawlProxy: Unit = {
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
  }
  import scala.util.{Failure, Success}
  import com.neo.sk.reptile.core.Increment.{IncrementByTime,Increment}
  import com.neo.sk.reptile.core.spider.spiderHeader.buildHeader
  import com.neo.sk.reptile.models.dao.{rArticle,ArticleDao}

  case class imageListElem(img: String, title: String, note: String)

  def main(args: Array[String]): Unit = {
    val appId = 1
    val appName = "netEase"
    val appNameCn = "网易"
    val columnName = "sports"
    val columnNameCn  = "体育"


    var newsList = List.empty[NtesArticleElem]
    val increment = new IncrementByTime
//    increment.initial()
//    println(increment.isNew("16156561561"))
    val column = "http://sports.163.com/special/000587PR/newsdata_n_index.js?callback=data_callback"
//    fetch(column,None,None,None,"GBK").onComplete{
//      case Success(html) =>
//        parseColumn(html) match {
//          case Right(list: List[NtesArticleElem]) =>
//            newsList = list
//          case Left(e) =>
//            log.debug(s"parse column page failed, error=${e.entity}")
//        }
//      case Failure(e) =>
//        println(s"fetch error $e")
//    }
//    newsList.foreach{ news =>
//      fetch(news.docurl).onComplete{
//        case Success(n) =>
//          n match {
//            case Right(s) =>
//              val content = Jsoup.parse(s.entity)
//              content.getElementById("endText" ).hasText
//            case Left(error) =>
//
//          }
//        case Failure(e) =>
//      }
//    }
    val url = "http://travel.163.com/photoview/17KK0006/2143753.html"
    val url1 = "http://sports.163.com/19/0306/09/E9ITNPRM00058781.html"

    case class photoView()
    fetch(url,None,buildHeader("netEase")).onComplete{
      case Success(n) =>
        n match {
          case Right(s) =>
            val photoUrlRegex = "http(s)?://([.a-zA-Z]+).163.com/photoview/(.*).html".r
            val articleUrlRegex = "http(s)?://([.a-zA-Z]+).163.com/(\\d{2})/(\\d{4})/(\\d{2})(.*).html".r
            url match {
              case articleUrlRegex(a,b,c,d,e,f) =>
                val doc = Jsoup.parse(s.entity)
                val dateSource = doc.select("div.post_time_source")
                val time = TimeUtil.ntesDate2TimeStamp(getDate(dateSource.text()))
                val fromA = doc.getElementById("ne_article_source").text()
                val from = if (fromA.nonEmpty ) Some(fromA) else None
                val articleDiv = doc.select("div.post_text").first().children()
                val author = doc.getElementsByClass("left").text()
                val regex = "作者：(\\D+)".r
                val realAuthor = regex.findFirstIn(author)
                println(getDate(dateSource.text()), fromA,author,realAuthor)
                //            if (content.getElementsByClass("post_text") != null)
                //              println(content.getElementsByClass("post_text"))
                val content = doc.getElementById("endText").toString
                val title = doc.select("div.post_content_main").select("h1").text()
                val article = rArticle(-1,appId,appName,appNameCn,columnName,columnNameCn,title,content,articleDiv.html(),time,from,realAuthor,None,url)
                println(rArticle)
                ArticleDao.addArticle(article).onComplete{
                  case Success(a) => println(s"add article successfully $a")
                  case Failure(e) => println(s"add article error $e")
                }
              case photoUrlRegex(a,b,c) =>
                val doc = Jsoup.parse(s.entity)
                val images = doc.getElementsByTag("textarea").toString
                val title = doc.select("div.headline").select("h1").text()
                val time = TimeUtil.dateYYMMdd2TimeStamp(doc.select("div.headline").select("span").text())
                var imageList = List.empty[String]
                val list = transImageList2Json(images)
                decode[List[imageListElem]](list) match {
                  case Right(rsp) =>
                    imageList = rsp.map(_.img)
                    println(imageList.toString())
                  case Left(e) => println(s"decode error: $e")
                }
                println(title,time)
                val article = rArticle(-1,appId,appName,appNameCn,columnName,columnNameCn,title,imageList.toString(),"",time,None,None,Some(imageList.toString()),url)

              case _ => println("lalala")
            }

          case Left(error) =>
            println(s"error : $error")
        }
      case Failure(e) =>
        println(s"e : $e")
    }
    println(s"end")
  }
}
