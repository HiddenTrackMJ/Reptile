package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.reptile.models.SlickTables._
import com.neo.sk.reptile.Boot.executor
import com.neo.sk.reptile.common.Constant.{AbnormalType, CoinRecordType}
import com.neo.sk.utils.EhCacheApi

/**
  * Created by dry on 2018/5/5.
  */
object AbnormalDAO {

  def addUseWithoutRentUsage(uid: Long, eId: Long, eName: String, startTime: Long, endTime: Long, fine: Double, uName: String, gIp: String) = {
    val action = for {
      r1 <- tUsers.filter(_.id === uid).map(_.coin).result.head
      r2 <- tUsers.filter(_.id === uid).map(_.coin).update(r1 - fine)
      r3 <- tAbnormalUsage += rAbnormalUsage(-1l, AbnormalType.useWithoutRent, Some(uid), eId, eName, Some(startTime), Some(endTime),
        (endTime - startTime).toInt, fine = fine, userName = uName, equIp = gIp)
      r4 <- tCoinsRecord += rCoinsRecord(uid, CoinRecordType.consumption, fine, "用了没租罚款", None, System.currentTimeMillis())
    } yield {
      (r1, r2, r3, r4)
    }
    db.run(action.transactionally)
  }

  def addRentWithoutUseUsage(uid: Long, eId: Long, eName: String, startTime: Long, duration: Int, fine: Double, uName: String, gIp: String) = {
    val action = for {
      r1 <- tUsers.filter(_.id === uid).map(_.coin).result.head
      r2 <- tUsers.filter(_.id === uid).map(_.coin).update(r1 - fine)
      r3 <- tAbnormalUsage += rAbnormalUsage(-1L, AbnormalType.rentWithoutUse, Some(uid), eId, eName, Some(startTime), duration = duration, fine = fine, userName = uName, equIp = gIp)
      r4 <- tCoinsRecord += rCoinsRecord(uid, CoinRecordType.consumption, fine, "使用不足罚款", None, System.currentTimeMillis())
    } yield {
      (r1, r2, r3, r4)
    }
    db.run(action.transactionally)
  }

  def getHistoryAbnormal() = db.run {
    tAbnormalUsage.result
  }

  def addProcessKillMessage(processId: Long, linux: String, ip: String, name: String, reason: String, creatTime: Long) =
    db.run(tProcessKill += rProcessKill(-1, processId, linux, ip, name, reason, creatTime))

  def getAllProcessKill() = db.run {
    tProcessKill.result
  }

}
