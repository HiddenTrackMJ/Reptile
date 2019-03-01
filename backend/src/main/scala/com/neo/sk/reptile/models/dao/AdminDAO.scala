package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.db
import com.neo.sk.reptile.models.SlickTables._
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.reptile.Boot.executor
/**
  * Created by Lty on 18/4/28 6PM
  */
object AdminDAO {

  def getAdminByName(adminName: String) = {
    db.run(
      tAdmin.filter(i => i.account === adminName).result.headOption
    )
  }

  def addUser(username: String, password: String, email: String, linux: String, coinstan: Int, creatime: Long) = {
    db.run(tUsers += rUsers(1, username, password, email, linux, coinstan, creatime))
  }

  def queryuser(username: String) = {
//    val a = for {user <- tUsers
//                 if (user.username === username)} yield user.id
//    db.run(a.result)
    db.run(tUsers.filter(_.username === username).map(_.id).result)
  }

  def getuserInfo() = {
    db.run(tUsers.result)
  }

  def getcoinInfo()={
    db.run(tCoinsRecord.result)
  }

  def deleteUser(userId: Long) = {
    UserDAO.cleanUserCache(userId)
    val action = for{
      r1 <- tUsers.filter(_.id === userId).delete
      r2 <- tCoinsRecord.filter(_.uid === userId).delete
    } yield{
      (r1,r2)
    }
    db.run(action.transactionally)
  }

  def updateUser(id:Long,userName: String,email: String, linux: String, coinstan: Int) = {
    UserDAO.cleanUserCache(id)
    db.run(tUsers.filter(i => i.id === id).map(i => (i.email, i.linuxAccount, i.coinStandard)).update(email, linux, coinstan))
  }

    def queryStandard(userId:Long)={
    db.run(tUsers.filter(i=>i.id===userId).map(i=>(i.id,i.coinStandard)).result.head)
  }

  def queryUser(ID:Long)={
    val a=for{users<- tUsers
                if(users.id===ID)} yield users.username
    db.run(a.result.head)
  }

  def changeCoin(userId: Long, Coin: Double) = {
    UserDAO.cleanUserCache(userId)
    val a = for {
      i <- tUsers.filter(_.id === userId).map(_.coin).result.head
      j <- tUsers.filter(_.id === userId).map(_.coin).update(i + Coin)
    } yield j
    db.run(a)
  }

  def addRecord(uId:Long,recordtype:Int,coins:Double,remark:String,orderId:Option[Long],time:Long)={
    db.run(tCoinsRecord+=rCoinsRecord(uId,recordtype,coins,remark,None,time))
  }

  def deleteRecord(uId:Long,time:Long)={
    db.run(tCoinsRecord.filter(i=>i.uid===uId&&i.createTime===time).delete)
  }

  def standardEdit(uId:Long,oldStandard:Int,newStandard:Int,remark:String,creatTime:Long)={
    db.run(tCoinsStandardRecord+=rCoinsStandardRecord(uId,oldStandard,newStandard,remark,creatTime))
  }

  def main(args: Array[String]): Unit = {

    Thread.sleep(5000)
  }
}

