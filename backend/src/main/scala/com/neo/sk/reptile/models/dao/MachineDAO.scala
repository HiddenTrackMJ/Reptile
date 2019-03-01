package com.neo.sk.reptile.models.dao

import com.neo.sk.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.reptile.models.SlickTables._

/**
  * Created by dry on 2018/5/12.
  */
object MachineDAO {

  def getAllMachine() = db.run {
    tMachine.sortBy(_.priority.desc).map(i => (i.id, i.name)).result
  }

  def updateMachine(machineId:Long, name:String, ip:String, priority:Int) = db.run(
    tMachine.filter(_.id === machineId).map(r => (r.ip, r.priority)).update(
      (ip, Some(priority))
    )
  )

}
