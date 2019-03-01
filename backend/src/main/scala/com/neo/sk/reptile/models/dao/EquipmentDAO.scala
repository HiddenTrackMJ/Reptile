package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.reptile.models.SlickTables._
import com.neo.sk.reptile.Boot.executor
import com.neo.sk.reptile.common.Constant.EquipState
import com.neo.sk.utils.EhCacheApi

/**
  * Created by dry on 2018/4/27.
  **/

object EquipmentDAO {

  private val gpuCache = EhCacheApi.createCache[Option[rGpuEquipment]]("gpuCache", 1200, 1200)

  def getAllMachine() = db.run {
    tMachine.sortBy(_.priority.desc).result
  }

  def getGPUInfo(machineID: Long) = db.run {
    tGpuEquipment.filter(i => i.machineId === machineID).sortBy(_.name.asc).result
  }

  def getEquipInfoByName(ip: String, name: String) = {
    gpuCache.apply(s"gpu_${ip}_$name", () =>
      db.run {
        tGpuEquipment.filter(i => i.machineIp === ip && i.name === name).result.headOption
      })
  }

  def getMachineInfo(machineId: Long) = {
    db.run {
      tMachine.filter(i => i.id === machineId).result.head
    }
  }


  def getGpuByMachine(mid: Long) =
    db.run{
      tGpuEquipment.filter(_.machineId === mid).map(i => (i.name, i.fee)).result
    }

  def getEquipById(id:Long) = db.run(
    tGpuEquipment.filter(_.id === id).result.headOption
  )

  def addGPU(
                machineId:Long,
                machineIp: String,
                name: String,
                fee: Int,
                createTime: Long,
                memory: Int
              ) = db.run {
    gpuCache.remove(s"gpu_${machineIp}_$name")
    //    tEquipment.returning(tEquipment.map(_.id)) += rEquipment(-1L, name, description, ip, port, fee, createTime)
    tGpuEquipment += rGpuEquipment(-1L, machineId, machineIp, name, fee, createTime, memory)
  }

  def addMachine(
                name:String,
                ip:String,
                prior:Int
              ) = db.run {
    //    tEquipment.returning(tEquipment.map(_.id)) += rEquipment(-1L, name, description, ip, port, fee, createTime)
    tMachine += rMachine(-1L, name, ip, Some(prior))
  }

  def deleteGPUById(
                 id:Long
                 ) = db.run(
    tGpuEquipment.filter(_.id === id).delete
  )

  def deleteMachineById(
                       id:Long
                     ) = db.run(
    tMachine.filter(_.id === id).delete
  )

  def updateGPU(id:Long, name:String, fee:Int, memory:Int) = {
    db.run(
      tGpuEquipment.filter(_.id === id).map(r => (r.name, r.fee, r.memory)).update(
        (name, fee, memory)
      )
    )
  }

  def updateGPUByMachineId(machineId:Long, ip:String) = {
    db.run(
      tGpuEquipment.filter(_.machineId === machineId).map(r => (r.machineIp)).update(
        (ip)
      )
    )
  }

//  def getAvaiGpu()= {
//    db.run{
//      tGpuEquipment.map(_.id).result
//    }
//  }
  def getAllGpu() = db.run {
    tGpuEquipment.result
  }

  def getGpuByIp(ip: String) = db.run{
    tGpuEquipment.filter(_.machineIp === ip).result
  }

  def getPriceById(id: Long) = db.run {
    tGpuEquipment.filter(_.id === id).result.head
  }

}
