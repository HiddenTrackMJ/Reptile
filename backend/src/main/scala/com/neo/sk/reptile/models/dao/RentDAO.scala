package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.reptile.models.SlickTables._
import com.neo.sk.reptile.common.Constant.{CoinRecordType, OrderState}
import com.neo.sk.reptile.Boot.executor

import scala.concurrent.Future
/**
  * Created by dry on 2018/4/27.
  **/
object RentDAO {

  private val oneDay = 24 * 60 * 60 * 1000

  def getUserRent(uid: Long) = {
    val action = for {
      i <- tRentOrder.filter(_.userId === uid).sortBy(_.id.desc).result
      j <- tRentOrder.filter(_.userId === uid).length.result
    } yield {
      (i, j)
    }

    db.run(action.transactionally)
  }

  def checkGPU(gpuId: Long) = db.run(
    tGpuEquipment.filter(_.id === gpuId).result.headOption
  )

  def getUserRentRecord(uid: Long) = {
    val action = {
      tRentOrder join tGpuEquipment on { (rent, gpu) =>
        rent.equId === gpu.id &&
          (rent.userId === uid)
      }
    }.result

    db.run(action)
  }

  def getUserRentByTime(startTime: Long, userId: Long, mId: Long) = {
    val action = for {
      i <- tGpuEquipment.filter(_.machineId === mId).sortBy(_.id).result
      j <- tRentOrder.filter(k => k.state =!= OrderState.canceled && (k.startTime < startTime + oneDay && k.endTime > startTime)).result
      k <- tUsers.filter(_.id === userId).result.head
    } yield {
      (i, j, k)
    }

    db.run(action.transactionally)
  }

  def getRentInfo(eid: Long, time: Long) = db.run {
    tRentOrder.filter(i => i.equId === eid && i.startTime <= time && i.endTime >= time && i.state =!= OrderState.canceled).result.headOption
  }

  def getARent(start: Long, end: Long) = db.run{
    tRentOrder.filter(r => r.state =!= OrderState.canceled && (r.startTime >= start && r.endTime <= end) || (r.startTime <= start && r.endTime >= end )).map(i =>i.equId).result
  }

  def getUserEmail(eid:Long,time:Long)=db.run{
    tRentOrder.filter(i => i.equId === eid && i.startTime <= time && i.endTime >= time && i.state =!= OrderState.canceled).map(r => (r.email, r.state)).result.headOption
  }

  def getEquAndCoin(equ: Long, userId: Long) = {
    val action = for {
      i <- tGpuEquipment.filter(_.id === equ).result
      j <- tUsers.filter(_.id === userId).result
    } yield {
      (i, j)
    }

    db.run(action.transactionally)
  }

  def checkTime(equId: Long, start: Long, end: Long) = db.run {
    tRentOrder.filter(i => i.state =!= OrderState.canceled && i.equId === equId && i.startTime < end && i.endTime > start).exists.result
  }

  def addRent(rent: rRentOrder) = {
    val action = for {
      i <- tRentOrder.returning(tRentOrder.map(_.id)) += rent
      j <- tUsers.filter(_.id === rent.userId).map(_.coin).result.head
      k <- tUsers.filter(_.id === rent.userId).map(_.coin).update(j - rent.costCoins)
      r <- tCoinsRecord += rCoinsRecord(rent.userId, CoinRecordType.consumption, rent.costCoins, "租用设备", Some(i), rent.createTime)
      l <- tRentRecord.returning(tRentRecord.map(_.id)) += rRentRecord(-1l, rent.userId, rent.userName, rent.equId, rent.startTime, rent.endTime, rent.costCoins, rent.state,
        rent.createTime, rent.returnCoins, rent.equIp, rent.equName, rent.linuxAccount, rent.email)
    } yield {
      (i, j, k, r, l)
    }

    db.run(action.transactionally)
  }

  def cancelRent(userId: Long, equId: Long, startTime: Long, endTime: Long, returnCoins: Double, state: Int) = {
    val action = for {
      i <- tRentOrder.filter(i => i.userId === userId && i.equId === equId && i.startTime === startTime).sortBy(_.id.desc).result.head
      j <- tRentOrder.filter(_.id === i.id).map(i => (i.endTime, i.costCoins, i.state, i.returnCoins)).update(endTime, f"${i.costCoins - returnCoins}%.2f".toDouble, state, Some(f"$returnCoins%.2f".toDouble))
      //      p <- {println(f"${i._1 - (if(state == OrderState.canceled) 0 else returnCoins)}%.2f".toDouble + " " + i._1 + " " + (if(state == OrderState.canceled) 0 else returnCoins));DBIO.successful(1)}
      k <- tUsers.filter(_.id === userId).map(_.coin).result.head
      l <- tUsers.filter(_.id === userId).map(_.coin).update(f"${k + returnCoins}%.2f".toDouble)
      r <- tCoinsRecord += rCoinsRecord(userId, CoinRecordType.income, f"$returnCoins%.2f".toDouble, "取消租用退款", Some(i.id), System.currentTimeMillis())
      m <- tRentRecord.returning(tRentRecord.map(_.id)) += rRentRecord(-1l, i.userId, i.userName, i.equId, i.startTime, endTime, f"${i.costCoins - returnCoins}%.2f".toDouble, state,
        System.currentTimeMillis(), Some(f"$returnCoins%.2f".toDouble), i.equIp, i.equName, i.linuxAccount, i.email)
    } yield {
      (i, j, k, l, r, m)
    }

    db.run(action.transactionally)
  }

  def cancelRentBySystem(rentId: Long, endTime: Long, state: Int) = {
    val action = for {
      i <- tRentOrder.filter(_.id === rentId).map(i => (i.endTime, i.state)).update((endTime, state))
      j <- tRentOrder.filter(_.id === rentId).result.head
      m <- tRentRecord.returning(tRentRecord.map(_.id)) += rRentRecord(-1l, j.userId, j.userName, j.equId, j.startTime, j.endTime, j.costCoins, j.state,
        System.currentTimeMillis(), Some(0), j.equIp, j.equName, j.linuxAccount, j.email)
    } yield {
      (i, j, m)
    }

    db.run(action.transactionally)
  }


  def getRentByMid(mid: Long, startTime: Long) = {
    val action = for {
      i <- tGpuEquipment.filter(_.machineId === mid).sortBy(_.id).result
      j <- tRentOrder.filter(k => k.state =!= OrderState.canceled && (k.startTime < startTime + oneDay && k.endTime > startTime)).result
    } yield {
      (i, j)
    }

    db.run(action.transactionally)
  }

  def getEquFee(equID: Long) = {
    db.run(tGpuEquipment.filter(_.id === equID).map(_.fee).result)
  }

  def getUserRent(userID: Long, equID: Long) = {
    db.run(tRentOrder.filter(i => i.userId === userID && i.equId === equID).map(_.endTime).result)
  }

  def continueRent(userID: Long, userName: String, equId: Long, startTime: Long, endTime: Long, costCoins: Double, state: Int, createTime: Long,
                   returnCoin: Option[Double], equIp: String, equName: String, linuxAccount: String, email: String) = {
    db.run(tRentOrder += rRentOrder(
      -1L, userID, userName, equId, startTime, endTime, costCoins, state, createTime, None, equIp, equName, linuxAccount, email))
  }

  def continueFee(userID: Long, fee: Double, time: Long) = {
    val a = for {
      i <- tUsers.filter(_.id === userID).map(_.coin).result.head
      j <- tUsers.filter(_.id === userID).map(_.coin).update(i - fee)
      k <- tCoinsRecord += rCoinsRecord(userID, 0, fee, "超时续约", None, time)
    } yield k
    db.run(a)
  }

  def getBeforRent(equId: Long, time: Long) = db.run{
    tRentOrder.filter(i => i.state =!= OrderState.canceled && i.equId === equId && i.startTime <= time && i.endTime >= time).result.headOption
  }

}
