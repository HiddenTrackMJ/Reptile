package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.driver.api._
import com.neo.sk.utils.DBUtil.db
import com.neo.sk.reptile.models.SlickTables._

import scala.concurrent.Future

/**
  * User: Jason
  * Date: 2019/3/27
  * Time: 11:13
  */
object CommentDAO {
  def create(): Future[Unit] = {
    db.run(tComment.schema.create)
  }

  def getAllComment: Future[Seq[tComment#TableElementType]] = {
    db.run{
      tComment.result
    }
  }

  def getLatestTime: Future[Option[Long]] = {
    db.run{
      tComment.map(_.postTime).max.result
    }
  }

  def addComment(comments: Iterable[rComment]): Future[Option[Int]] = {
    db.run(
      tComment.++=(comments)
    )
  }

  def deleteComment(id: Long):Future[Int] ={
    db.run{
      tComment.filter(_.commentId === id).delete
    }
  }
  def main(args: Array[String]): Unit = {
    println("starting")
    //    create()
    addComment(Iterable(rComment(-1,1,"sina","新浪","sports",
      "体育","www",123456,"html",None,None,None,"159","asd")))
    //    val b = deleteArticle("www")
    val a = getAllComment
    println("create")
    Thread.sleep(5000)
    println(a)
    println("end")
  }
}
