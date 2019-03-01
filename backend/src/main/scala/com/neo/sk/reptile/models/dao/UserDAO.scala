package com.neo.sk.reptile.models.dao

import com.neo.sk.reptile.common.Constant.CoinRecordType
import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.reptile.models.SlickTables._
import com.neo.sk.reptile.Boot.executor
import com.neo.sk.reptile.models.SlickTables
import com.neo.sk.utils.EhCacheApi
import scala.concurrent.Future

/**
  * Created by Anjiansan on 2018/04/28.
  **/
object UserDAO {

  private val userCache = EhCacheApi.createCache[Option[rUsers]]("userCache", 1200, 1200)


  def getUserByName(userName: String) = db.run {
    tUsers.filter(_.username === userName).result.headOption
  }

  def getUserByLinuxAccount(linuxAccount: String) = {
      db.run(tUsers.filter(i => i.linuxAccount like "%" + linuxAccount + "%").map(_.id).result)
  }

  def getUserEmail(uid: Long) = db.run(
    tUsers.filter(_.id === uid).map(_.email).result.headOption
  )

  def cleanUserCoin() = db.run {
    tUsers.filter(_.coin > 0.0).map(_.coin).update(0)
  }

  def getAllUser() = db.run {
    tUsers.result
  }

  def getUserInSet(idSet:Set[Long])={
    tUsers.filter(_.id.inSet(idSet)).result
  }

  def updateUserCoin(uid: Long, oldCoin: Double, coinStandard: Int) = {
    val action = for {
      r1 <- tUsers.filter(_.id === uid).map(_.coin).update(oldCoin + coinStandard)
      r2 <- tCoinsRecord += rCoinsRecord(uid, CoinRecordType.income, coinStandard, "系统每周自动充值", None, System.currentTimeMillis())
    } yield {
      r1
    }
    db.run(action.transactionally)
  }

  def getCleanUser() = db.run {
    tUsers.filter(_.coin > 0.0).result
  }

  def cleanUserCoin(uid: Long, oldCoin: Double) = {
    val action = for {
      r1 <- tUsers.filter(_.id === uid).map(_.coin).update(0)
      r2 <- tCoinsRecord += rCoinsRecord(uid, CoinRecordType.consumption, oldCoin, "系统每周自动清零", None, System.currentTimeMillis())
    } yield {
      r1
    }
    db.run(action.transactionally)
  }

  def getUserById(id: Long) = {
    userCache.apply(s"userId_$id", () =>
      db.run {
        tUsers.filter(_.id === id).result.headOption
      })
  }

  def changePwd(userName:String,passWord:String)={
    db.run(tUsers.filter(_.username===userName).map(_.securePwd).update(passWord))
  }

  def queryTime(userName:String)={
    db.run(tUsers.filter(_.username===userName).map(_.createTime).result.head)
  }

  def querySalary()={
    db.run(tUsers.map(i=>(i.username,i.coinStandard)).result)
  }

  def getCoinRecord() = {
    db.run(tCoinsRecord.filter(i => i.recordType === 2 || i.recordType === 3).joinLeft(SlickTables.tUsers).on(_.uid === _.id).result)
  }

  def getStandardRecord()={
    db.run(tCoinsStandardRecord.join(SlickTables.tUsers).on(_.uid===_.id).result)
    db.run(tCoinsStandardRecord.join(SlickTables.tUsers).on(_.uid===_.id).result)
  }

//  def getStandardRecord()={
//    db.run(tCoinsStandardRecord.result)
//  }

  def getStandardRecordInset(l:Set[Long])={
    db.run(tCoinsStandardRecord.filter(_.uid.inSet(l)).result)
  }

  def cleanUserCache(Id:Long)={
    userCache.remove(s"userId_$Id")
  }

}
