package com.neo.sk.reptile.core.parser

import akka.actor.typed.ActorRef
import com.neo.sk.reptile.core.spider
import com.neo.sk.reptile.models._
import com.neo.sk.reptile.core.spider._
import com.neo.sk.reptile.core.task._
import com.neo.sk.reptile.core.Increment._
import com.neo.sk.reptile.common.Constant._
import com.neo.sk.reptile.core.task
import com.neo.sk.reptile.models
import com.neo.sk.utils.TimeUtil
import javax.swing.text.html.parser.Entity
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

/**
  * User: Jason
  * Date: 2019/2/28
  * Time: 10:59
  * 网易新闻解析
  */
class NetEaseParser(app:NewsApp, newsAppColumn:NewsAppColumn, wrapper:ActorRef[spider.SpiderRst], increment:Increment) extends Parser {

  import io.circe.parser.decode
  import io.circe.parser.parse
  import io.circe.Decoder
  import io.circe.generic.auto._

  private val log = LoggerFactory.getLogger(this.getClass)

  private def getDate(s:String) = {
    val re = "(\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)(.*)".r
    s match {
      case re(y,m,d,h,mm,ss,_) =>
        s"${y}年${m}月${d}日 $h:$mm:$ss"

      case _ =>
        ""
    }
  }

  private def getFrom(s:String) = {
    val re = "(\\d+)年(\\d+)月(\\d+)日 (\\d+):(\\d+)　来源：([\\u4e00-\\u9fa5]*) (.*)".r
    s match {
      case re(y,m,d,h,mm,from,_) =>
        s"$from"

      case _ =>
        ""
    }
  }

  private def genNextPage(domain:String,ids:String,column:String,
    currentPage:Int,
    rsp:List[NtesArticleElem]) :Option[String] = {
    val p = currentPage + 1
    val nextPage = if(p < 6) "0"+p else p.toString
    if(rsp.count(t => increment.isNew(TimeUtil.dateMMddYY2TimeStamp(t.time).toString)) == rsp.length)
      Some(s"http://$domain/special/$ids/${column}_$nextPage.js?callback=data_callback")
    else
      None
  }

  case class imageListElem(img: String, title: String, note: String)

  private def parseArticlePage(str:String,url:String): Either[spider.SpiderTaskError, Article] = {
    try {
      val photoUrlRegex = "http(s)?://([.a-zA-Z]+).163.com/photoview/(.*).html".r
      val articleUrlRegex = "http(s)?://([.a-zA-Z]+).163.com/(\\d{2})/(\\d{4})/(\\d{2})(.*).html".r
      url match {
        case articleUrlRegex(a,b,c,d,e,f) =>
          val doc = Jsoup.parse(str)
          val dateSource = doc.select("div.post_time_source")
          val time = TimeUtil.ntesDate2TimeStamp(getDate(dateSource.text()))
          val fromA = doc.getElementById("ne_article_source").text()
          val from = if (fromA.nonEmpty ) Some(fromA) else None
          val articleDiv = doc.select("div.post_text").first().children()
          val author = doc.getElementsByClass("left").text()
          val regex = "作者：(\\D+)".r
          val realAuthor = regex.findFirstIn(author)
          val content = doc.getElementById("endText").toString
          val title = doc.select("div.post_content_main").select("h1").text()
          Right(Article(app.id,app.name,app.nameCn,newsAppColumn.name,newsAppColumn.nameCn,title,content,articleDiv.html(),time,from,realAuthor,None,url))

        case photoUrlRegex(a,b,c) =>
          val doc = Jsoup.parse(str)
          val images = doc.getElementsByTag("textarea").toString
          val title = doc.select("div.headline").select("h1").text()
          val time = TimeUtil.dateYYMMdd2TimeStamp(doc.select("div.headline").select("span").text())
          var imageList = List.empty[ArticleImage]
          val list = transImageList2Json(images)
          decode[List[imageListElem]](list) match {
            case Right(rsp) =>
              imageList = rsp.map(r => ArticleImage(r.img,Some(r.title)))
              println(imageList.toString())
            case Left(e) => println(s"decode error: $e")
          }
          Right(Article(app.id,app.name,app.nameCn,newsAppColumn.name,newsAppColumn.nameCn,title,imageList.toString(),"",time,None,None,Some(imageList),url))

        case _ => Left(SpiderTaskError(s"url match failed",-5))
      }

    }catch {
      case e:Exception =>
        log.debug(s"parse article failed,error=${e.getMessage} and content=\n$str")
        Left(SpiderTaskError(s"parse article failed,error=${e.getMessage}",-3))
    }
  }

  override def parseArticle(rst: SpiderRst): Either[spider.SpiderTaskError, Article] = {
    rst.rst match {
      case Right(r) =>
        if(r.code == 200){
          parseArticlePage(r.entity,rst.task.url)

        }else{
          Left(SpiderTaskError(s"response code=${r.code} is not valid,entity=${r.entity}",code = r.code))
        }
      case Left(e) =>
        Left(e)
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

        case _ =>
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
    val x = entity.substring(first+1,end)
    x
  }

  private def transImageList2Json(entity:String):String = {
    val first = entity.indexOf("[")
    val end = entity.lastIndexOf("]")
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

  case class NtesArticleElem(docurl: String, commenturl: String, newstype: String, label: String, time: String)

  private def parseColumnPage(domain:String,
    ids:String,
    column:String,
    currentPage:Int,
    entity:String
  ) : Either[spider.SpiderTaskError, List[Task]] = {
    try{
      val jsonStr = transEntity2Json(entity)
      decode[List[NtesArticleElem]](jsonStr) match {
        case Right(rsp) =>
          //          println(rsp)
          val articleTasks = rsp.filter(t => t.newstype == "article" && t.time.length > 0 && isArticleUrl(t.docurl))
            .filter(t =>increment.isNew(TimeUtil.dateMMddYY2TimeStamp(t.time).toString)).map{ a =>
            Task(spider.SpiderTask(a.docurl, None, spider.TaskType.articlePage,None,None,wrapper,code = app.articleParseCode,
              headerOpt = spider.spiderHeader.buildHeader(app.name)),TaskPriority.article,app.useProxy)
          }
          val commentTasks = rsp.filter(t => t.newstype == "article" && t.time.length > 0 && isArticleUrl(t.docurl))
            .filter(t =>increment.isNew(TimeUtil.dateMMddYY2TimeStamp(t.time).toString)).map{ a =>
            Task(spider.SpiderTask(a.docurl, Some(a.commenturl),spider.TaskType.firstComment,None,None,wrapper,code = app.articleParseCode,
              headerOpt = spider.spiderHeader.buildHeader(app.name)),TaskPriority.firstComment,app.useProxy)
          }
          val columnTaskOpt = genNextPage(domain,ids,column,currentPage,
            rsp.filter(t => t.newstype == "article" && t.time.length > 0 && isArticleUrl(t.docurl))).map{ u =>
            Task(spider.SpiderTask(u, None, spider.TaskType.columnPage,None,None,wrapper,code = app.columnParseCode,
              headerOpt = spider.spiderHeader.buildHeader(app.name)),TaskPriority.column,app.useProxy)
          }

          if(columnTaskOpt.isDefined){
            Right(columnTaskOpt.get :: articleTasks ::: commentTasks)
          }else{
            Right(articleTasks ::: commentTasks)
          }

        case Left(error) =>
          Left(SpiderTaskError(s"parse json error=$error",code = -3))
      }

    }catch {
      case e: Exception =>
        log.debug(s"parse column page failed, error=${e.getMessage}")
        Left(SpiderTaskError(s"parse column page failed, error=${e.getMessage}",-2))
    }
  }

  override def parseColumn(rst: SpiderRst): Either[spider.SpiderTaskError, List[task.Task]] = {
    val spiderTask = rst.task
    rst.rst match {
      case Right(r:SpiderTaskSuccess) =>
        if(r.code == 200){
          parseColumnUrl(spiderTask.url) match {
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

  def getFirstComment(entity: String, task: SpiderTask): Either[SpiderTaskError, (List[Either[SpiderTaskError, Comment]], List[Task])] = {
    var pages:Int = 0
    decode[cid](entity) match {
      case Right(rsp)   =>
        pages = rsp.newListSize / 30 + 1
        var cuList = List.empty[String]
        if(pages > 1){
          for(i <- 2 to pages){
            val c = (i - 1) * 30
            val b = task.url + s"?ibc=newspc&limit=30&showLevelThreshold=72&headLimit=1&tailLimit=2&offset=$c"
            cuList = cuList :+ b
          }
        }
        println(cuList)
        val reg = """"against":(.*?)"vote":([.0-9]+)""".r
        val comments = entity.split("\"comments\":").last.split("},\"newListSize\"").apply(0).drop(1)
        val commentId = rsp.commentIds.filter(p =>
          p.contains(",")).flatMap(p =>{
          p.split(",").toList.map(p => p.toLong)
        })
        val jsonStr = reg.findAllIn(comments).toList.map(l => "{" + l + "}")
        val a = jsonStr.map { j =>
          decode[comment](j) match {
            case Right(r) =>
              val reply = findReply(r.commentId, commentId)
              val cmt = models.Comment(app.id,app.name,app.nameCn,newsAppColumn.name,newsAppColumn.nameCn, r.content, TimeUtil.dateYYMMdd2TimeStamp(r.createTime),
                r.source, r.user.nickname, r.user.avatar, task.url, task.commentUrl.getOrElse(task.url), reply, r.commentId, r.buildLevel )
              Right(cmt)
            case Left(error)  =>
              Left(SpiderTaskError(s"parse comment failed, error=${error.getMessage}",-2))
          }
        }
        val commentTask = cuList.map(c => Task(spider.SpiderTask(c, task.commentUrl, TaskType.comment,None,None,wrapper,code = app.articleParseCode,
          headerOpt = spider.spiderHeader.buildHeader("netEase")),TaskPriority.comment))
        Right(a, commentTask)

      case Left(error)  =>
        Left(SpiderTaskError(s"parse comment failed, error=${error.getMessage}",-2))
    }
  }

  def getComment(entity: String, task: SpiderTask): Either[SpiderTaskError, (List[Either[SpiderTaskError, Comment]], List[Task])] = {
    decode[cid](entity) match {
      case Right(rsp)   =>
        val reg = """"against":(.*?)"vote":([.0-9]+)""".r
        val comments = entity.split("\"comments\":").last.split("},\"newListSize\"").apply(0).drop(1)
        val commentId = rsp.commentIds.filter(p =>
          p.contains(",")).flatMap(p =>{
          p.split(",").toList.map(p => p.toLong)
        })
        val jsonStr = reg.findAllIn(comments).toList.map(l => "{" + l + "}")
        val a = jsonStr.map { j =>
          decode[comment](j) match {
            case Right(r) =>
              val reply = findReply(r.commentId, commentId)
              val cmt = models.Comment(app.id,app.name,app.nameCn,newsAppColumn.name,newsAppColumn.nameCn, r.content, TimeUtil.dateYYMMdd2TimeStamp(r.createTime),
                r.source, r.user.nickname, r.user.avatar, task.url, task.commentUrl.getOrElse(task.url), reply, r.commentId, r.buildLevel )
              Right(cmt)
            case Left(error)  =>
              Left(SpiderTaskError(s"parse comment failed, error=${error.getMessage}",-2))
          }
        }
        Right(a, List.empty[Task])

      case Left(error)  =>
        Left(SpiderTaskError(s"parse comment failed, error=${error.getMessage}",-2))
    }
  }

  case class cid(commentIds: List[String], newListSize: Int)

  case class user(avatar: Option[String], nickname: Option[String], userId: Long)

  case class comment(buildLevel: Int, commentId: Long, content: String,
    createTime: String, user: user, source: String, vote: Int)

  private def findReply(a: Long, l: List[Long]) = {
    var x: Long = -1
    var i: Long = -1

    l.foreach(p => {
      if (p != a) x = p
      else i = x
    })

    i
  }

  override def parseComment(rst: SpiderRst) : Either[spider.SpiderTaskError,
    (List[Either[spider.SpiderTaskError, models.Comment]], List[task.Task])] = {
    val spiderTask = rst.task
    rst.rst match {
      case Right(success) =>
        if (spiderTask.taskType == TaskType.firstComment)
          getFirstComment(success.entity, spiderTask)
        else
          getComment(success.entity, spiderTask)

      case Left(error) =>
        Left(error)
    }
  }
}
