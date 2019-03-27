package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.driver.api._
import com.neo.sk.utils.DBUtil.db
import com.neo.sk.reptile.models.SlickTables._
import com.neo.sk.reptile.Boot.executor

import scala.concurrent.Future
//import scala.concurrent.duration.FiniteDuration

/**
  * User: Jason
  * Date: 2019/3/4
  * Time: 14:27
  */

//  case class rArticle(aid: Long, appId: Int, appName: String = "", appNameCn: String = "", columnName: String = "", columnNameCn: String = "", title: String = "", content: String = "", html: String = "", postTime: Long = 0L, src: Option[String] = None, author: Option[String] = None, srcImage: Option[String] = None, srcUrl: String = "")
//
//  trait tArticle {
//    class tArticle(_tableTag: Tag)  extends Table[rArticle](_tableTag, "article".toUpperCase) {
//
//      val aid: Rep[Long] = column[Long]("aid".toUpperCase, O.AutoInc, O.PrimaryKey)
//      val appId: Rep[Int] = column[Int]("app_id".toUpperCase)
//      val appName: Rep[String] = column[String]("app_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
//      val appNameCn: Rep[String] = column[String]("app_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
//      val columnName: Rep[String] = column[String]("column_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
//      val columnNameCn: Rep[String] = column[String]("column_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
//      val title: Rep[String] = column[String]("title".toUpperCase, O.Default(""))
//      val content: Rep[String] = column[String]("content".toUpperCase, O.Default(""))
//      val html: Rep[String] = column[String]("html".toUpperCase, O.Default(""))
//      val postTime: Rep[Long] = column[Long]("post_time".toUpperCase, O.Default(0L))
//      val src: Rep[Option[String]] = column[Option[String]]("src".toUpperCase, O.Length(32,varying=true), O.Default(None))
//      val author: Rep[Option[String]] = column[Option[String]]("author".toUpperCase, O.Length(32,varying=true), O.Default(None))
//      val srcImage: Rep[Option[String]] = column[Option[String]]("src_image".toUpperCase, O.Default(None))
//      val srcUrl: Rep[String] = column[String]("src_url".toUpperCase, O.Length(128,varying=true), O.Default(""))
//
//      def * = (aid, appId, appName, appNameCn, columnName, columnNameCn, title, content, html, postTime, src, author, srcImage, srcUrl) <> (rArticle.tupled, rArticle.unapply)
//    }
//    /** Collection-like TableQuery object for table tArticle */
//    //  lazy val tArticle = new TableQuery(tag => new tArticle(tag))
//    protected val tArticle = TableQuery[tArticle]
//  }
object ArticleDAO  {
  def create(): Future[Unit] = {
    db.run(tArticle.schema.create)
  }

  def getAllArticle: Future[Seq[tArticle#TableElementType]] = {
    db.run{
      tArticle.result
    }
  }

  def getLatestTime: Future[Option[Long]] = {
    db.run{
      tArticle.map(_.postTime).max.result
    }
  }

  def getLatestTimeBySource(appId:Int,column:String): Future[Option[Long]] = {
    db.run{
      tArticle.filter(t =>t.appId === appId && t.columnName === column).map(_.postTime).max.result
    }
  }

  def addArticle(articles: Iterable[rArticle]): Future[Option[Int]] = {
    db.run(
      tArticle.++=(articles)
    )
  }

  def deleteArticle(name:String):Future[Int] =
    db.run{
      tArticle.filter(_.title === name).delete
    }

  def main(args: Array[String]): Unit = {
    import scala.util.{Failure, Success}
    println("starting")
//    create().onComplete{
//      case Success(value) => println("1: " + value)
//      case Failure(e) => println( "1: " + e)
//    }
//    SpiderFailedTaskDAO.create().onComplete{
//      case Success(value) => println("2: " + value)
//      case Failure(e) => println( "2: " + e)
//    }
//    CommentDAO.create().onComplete{
//      case Success(value) => println("3: " + value)
//      case Failure(e) => println( "3: " + e)
//    }
//    addArticle(Iterable(rArticle(-1,1,"sina","新浪","sports","体育","www","qwer","html",123456,None,None,None,"asd")))
//      .onComplete{
//        case Success(value) => println("1: " + value)
//        case Failure(e) => println( "1: " + e)
//      }
    //    val b = deleteArticle("www")
    getAllArticle.onComplete{
      case Success(value) => println("2: " + value.map(_.title))
      case Failure(e) => println( "2: " + e)
    }
    getLatestTimeBySource(3, "netEase").onComplete{
      case Success(value) => println("2: " + value)
      case Failure(e) => println( "2: " + e)
    }
    println("create")
    Thread.sleep(5000)
//    println(a)
    println("end")
  }
}
