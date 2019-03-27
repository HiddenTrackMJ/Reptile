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
object SpiderFailedTaskDAO {
  def create(): Future[Unit] = {
    db.run(tSpiderFailedTask.schema.create)
  }

  def getAllSpiderFailedTask: Future[Seq[tSpiderFailedTask#TableElementType]] = {
    db.run{
      tSpiderFailedTask.result
    }
  }

  def getLatestTime: Future[Option[Long]] = {
    db.run{
      tSpiderFailedTask.map(_.createTime).max.result
    }
  }

  def addSpiderFailedTask(failedTask: rSpiderFailedTask): Future[Int] = {
    db.run(
      tSpiderFailedTask += failedTask
    )
  }

  def deleteSpiderFailedTask(id: Long):Future[Int] ={
    db.run{
      tSpiderFailedTask.filter(_.id === id).delete
    }
  }



}
