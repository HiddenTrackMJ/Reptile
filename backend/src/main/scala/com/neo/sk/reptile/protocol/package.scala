package com.neo.sk.reptile

import javax.mail.{Authenticator, PasswordAuthentication}
import com.neo.sk.reptile.models.SlickTables

import scala.collection.mutable

/**
  * Created by dry on 2018/4/26.
  */
package object protocol {

  case class MyAuthenticator(userName: String, password: String) extends Authenticator {

    override def getPasswordAuthentication: PasswordAuthentication = {
      new PasswordAuthentication(userName, password)
    }
  }

  case class EquipBaseInfo(

                            id: Int,
                            name: String,
                            Fan: String,
                            Temp: String,
                            Perf: String,
                            Pwr: String,
                            MemoryUsage: String,
                            GPUUtil: String,
                            Compute: String,
                            PID: Option[Int] = None,
                            Type: Option[String] = None,
                            ProcessName: Option[String] = None,
                            account: Option[String] = None
                          )

  case class ProcessInfo(
                          PID: Int,
                          Type: String,
                          ProcessName: String,
                          MemoryUsage: String,
                          account: Option[String] = None
                        )

  case class ReportInfo(
                         ip: String,
                         gpuId: Int,
                         pids: List[ProcessInfo]
                       )

  case class ReportRsp(
                        action: Option[Action],
                        errCode: Int = 0,
                        msg: String = "ok"
                      )

  case class Action(
                     action: String,
                     pid: List[Int]
                   )

  //(违规uid, 违规linux账号，第一次检测到的时间）
  case class EquipDangerousInfo(
                                 equip: SlickTables.rGpuEquipment,
                                 account: String,
                                 startTime: Long
                               )

  case class EquipActorInfo(
                             equip: SlickTables.rGpuEquipment,
                             isEmail: Boolean = false, //记录是否发送了结束提醒邮件
                             wasteTime: Int = 0,
                             isWasteMail: Int = 0,
                             alreadyRunningTime: mutable.HashMap[Int, Int] = mutable.HashMap(),
                             dangerousPid: mutable.HashMap[Int, Int] = mutable.HashMap()
                           )


}
