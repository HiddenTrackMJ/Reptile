package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.driver.api._
import com.neo.sk.utils.DBUtil.db

import scala.concurrent.Future
//import scala.concurrent.duration.FiniteDuration

/**
  * User: Jason
  * Date: 2019/3/4
  * Time: 14:27
  */

  case class rArticle(aid: Long, appId: Int, appName: String = "", appNameCn: String = "", columnName: String = "", columnNameCn: String = "", title: String = "", content: String = "", html: String = "", postTime: Long = 0L, src: Option[String] = None, author: Option[String] = None, srcImage: Option[String] = None, srcUrl: String = "")

  trait tArticle {
    class tArticle(_tableTag: Tag)  extends Table[rArticle](_tableTag, "article".toUpperCase) {

      val aid: Rep[Long] = column[Long]("aid".toUpperCase, O.AutoInc, O.PrimaryKey)
      val appId: Rep[Int] = column[Int]("app_id".toUpperCase)
      val appName: Rep[String] = column[String]("app_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
      val appNameCn: Rep[String] = column[String]("app_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
      val columnName: Rep[String] = column[String]("column_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
      val columnNameCn: Rep[String] = column[String]("column_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
      val title: Rep[String] = column[String]("title".toUpperCase, O.Default(""))
      val content: Rep[String] = column[String]("content".toUpperCase, O.Default(""))
      val html: Rep[String] = column[String]("html".toUpperCase, O.Default(""))
      val postTime: Rep[Long] = column[Long]("post_time".toUpperCase, O.Default(0L))
      val src: Rep[Option[String]] = column[Option[String]]("src".toUpperCase, O.Length(32,varying=true), O.Default(None))
      val author: Rep[Option[String]] = column[Option[String]]("author".toUpperCase, O.Length(32,varying=true), O.Default(None))
      val srcImage: Rep[Option[String]] = column[Option[String]]("src_image".toUpperCase, O.Default(None))
      val srcUrl: Rep[String] = column[String]("src_url".toUpperCase, O.Length(128,varying=true), O.Default(""))

      def * = (aid, appId, appName, appNameCn, columnName, columnNameCn, title, content, html, postTime, src, author, srcImage, srcUrl) <> (rArticle.tupled, rArticle.unapply)
    }
    /** Collection-like TableQuery object for table tArticle */
    //  lazy val tArticle = new TableQuery(tag => new tArticle(tag))
    protected val tArticle = TableQuery[tArticle]
  }
object ArticleDao extends tArticle {
  def create(): Future[Unit] = {
    db.run(tArticle.schema.create)
  }

  def getAllArticle: Future[Seq[tArticle#TableElementType]] = {
    db.run{
      tArticle.result
    }
  }

  def getLatesTime: Future[Option[Long]] = {
    db.run{
      tArticle.map(_.postTime).max.result
    }
  }

  def addArticle(article: rArticle): Future[Int] = {
    db.run(tArticle += article)
  }

  def deleteArticle(name:String):Future[Int] =
    db.run{
      tArticle.filter(_.title === name).delete
    }

  def main(args: Array[String]): Unit = {
    println("starting")
//    create()
//    addArticle(rArticle(-1,1,"sina","新浪","sports","体育","www","qwer","html",123456,None,None,None,"asd"))
//    val b = deleteArticle("www")
    val a = getAllArticle
    println("create")
    Thread.sleep(5000)
    println(a)
    println("end")
  }
}
