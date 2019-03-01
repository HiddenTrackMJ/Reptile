package com.neo.sk.reptile.models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.jdbc.PostgresProfile
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(tAbnormalUsage.schema, tAdmin.schema, tCoinsRecord.schema, tCoinsStandardRecord.schema, tGpuEquipment.schema, tMachine.schema, tProcessKill.schema, tRentOrder.schema, tRentRecord.schema, tUsers.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tAbnormalUsage
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param abnormalType Database column abnormal_type SqlType(int4)
    *  @param userId Database column user_id SqlType(int8), Default(None)
    *  @param equId Database column equ_id SqlType(int8)
    *  @param equName Database column equ_name SqlType(varchar), Length(255,true)
    *  @param startTime Database column start_time SqlType(int8), Default(None)
    *  @param endTime Database column end_time SqlType(int8), Default(None)
    *  @param duration Database column duration SqlType(int4)
    *  @param fine Database column fine SqlType(float8)
    *  @param userName Database column user_name SqlType(varchar), Length(255,true), Default()
    *  @param equIp Database column equ_ip SqlType(varchar), Length(255,true), Default() */
  case class rAbnormalUsage(id: Long, abnormalType: Int, userId: Option[Long] = None, equId: Long, equName: String, startTime: Option[Long] = None, endTime: Option[Long] = None, duration: Int, fine: Double, userName: String = "", equIp: String = "")
  /** GetResult implicit for fetching rAbnormalUsage objects using plain SQL queries */
  implicit def GetResultrAbnormalUsage(implicit e0: GR[Long], e1: GR[Int], e2: GR[Option[Long]], e3: GR[String], e4: GR[Double]): GR[rAbnormalUsage] = GR{
    prs => import prs._
      rAbnormalUsage.tupled((<<[Long], <<[Int], <<?[Long], <<[Long], <<[String], <<?[Long], <<?[Long], <<[Int], <<[Double], <<[String], <<[String]))
  }
  /** Table description of table abnormal_usage. Objects of this class serve as prototypes for rows in queries. */
  class tAbnormalUsage(_tableTag: Tag) extends profile.api.Table[rAbnormalUsage](_tableTag, "abnormal_usage") {
    def * = (id, abnormalType, userId, equId, equName, startTime, endTime, duration, fine, userName, equIp) <> (rAbnormalUsage.tupled, rAbnormalUsage.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(abnormalType), userId, Rep.Some(equId), Rep.Some(equName), startTime, endTime, Rep.Some(duration), Rep.Some(fine), Rep.Some(userName), Rep.Some(equIp)).shaped.<>({r=>import r._; _1.map(_=> rAbnormalUsage.tupled((_1.get, _2.get, _3, _4.get, _5.get, _6, _7, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column abnormal_type SqlType(int4) */
    val abnormalType: Rep[Int] = column[Int]("abnormal_type")
    /** Database column user_id SqlType(int8), Default(None) */
    val userId: Rep[Option[Long]] = column[Option[Long]]("user_id", O.Default(None))
    /** Database column equ_id SqlType(int8) */
    val equId: Rep[Long] = column[Long]("equ_id")
    /** Database column equ_name SqlType(varchar), Length(255,true) */
    val equName: Rep[String] = column[String]("equ_name", O.Length(255,varying=true))
    /** Database column start_time SqlType(int8), Default(None) */
    val startTime: Rep[Option[Long]] = column[Option[Long]]("start_time", O.Default(None))
    /** Database column end_time SqlType(int8), Default(None) */
    val endTime: Rep[Option[Long]] = column[Option[Long]]("end_time", O.Default(None))
    /** Database column duration SqlType(int4) */
    val duration: Rep[Int] = column[Int]("duration")
    /** Database column fine SqlType(float8) */
    val fine: Rep[Double] = column[Double]("fine")
    /** Database column user_name SqlType(varchar), Length(255,true), Default() */
    val userName: Rep[String] = column[String]("user_name", O.Length(255,varying=true), O.Default(""))
    /** Database column equ_ip SqlType(varchar), Length(255,true), Default() */
    val equIp: Rep[String] = column[String]("equ_ip", O.Length(255,varying=true), O.Default(""))
  }
  /** Collection-like TableQuery object for table tAbnormalUsage */
  lazy val tAbnormalUsage = new TableQuery(tag => new tAbnormalUsage(tag))

  /** Entity class storing rows of table tAdmin
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param account Database column account SqlType(varchar), Length(255,true)
    *  @param securePwd Database column secure_pwd SqlType(varchar), Length(255,true)
    *  @param createTime Database column create_time SqlType(int8) */
  case class rAdmin(id: Long, account: String, securePwd: String, createTime: Long)
  /** GetResult implicit for fetching rAdmin objects using plain SQL queries */
  implicit def GetResultrAdmin(implicit e0: GR[Long], e1: GR[String]): GR[rAdmin] = GR{
    prs => import prs._
      rAdmin.tupled((<<[Long], <<[String], <<[String], <<[Long]))
  }
  /** Table description of table admin. Objects of this class serve as prototypes for rows in queries. */
  class tAdmin(_tableTag: Tag) extends profile.api.Table[rAdmin](_tableTag, "admin") {
    def * = (id, account, securePwd, createTime) <> (rAdmin.tupled, rAdmin.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(account), Rep.Some(securePwd), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rAdmin.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column account SqlType(varchar), Length(255,true) */
    val account: Rep[String] = column[String]("account", O.Length(255,varying=true))
    /** Database column secure_pwd SqlType(varchar), Length(255,true) */
    val securePwd: Rep[String] = column[String]("secure_pwd", O.Length(255,varying=true))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tAdmin */
  lazy val tAdmin = new TableQuery(tag => new tAdmin(tag))

  /** Entity class storing rows of table tCoinsRecord
    *  @param uid Database column uid SqlType(int8)
    *  @param recordType Database column record_type SqlType(int4)
    *  @param coins Database column coins SqlType(float8)
    *  @param remark Database column remark SqlType(varchar), Length(512,true)
    *  @param orderId Database column order_id SqlType(int8), Default(None)
    *  @param createTime Database column create_time SqlType(int8) */
  case class rCoinsRecord(uid: Long, recordType: Int, coins: Double, remark: String, orderId: Option[Long] = None, createTime: Long)
  /** GetResult implicit for fetching rCoinsRecord objects using plain SQL queries */
  implicit def GetResultrCoinsRecord(implicit e0: GR[Long], e1: GR[Int], e2: GR[Double], e3: GR[String], e4: GR[Option[Long]]): GR[rCoinsRecord] = GR{
    prs => import prs._
      rCoinsRecord.tupled((<<[Long], <<[Int], <<[Double], <<[String], <<?[Long], <<[Long]))
  }
  /** Table description of table coins_record. Objects of this class serve as prototypes for rows in queries. */
  class tCoinsRecord(_tableTag: Tag) extends profile.api.Table[rCoinsRecord](_tableTag, "coins_record") {
    def * = (uid, recordType, coins, remark, orderId, createTime) <> (rCoinsRecord.tupled, rCoinsRecord.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uid), Rep.Some(recordType), Rep.Some(coins), Rep.Some(remark), orderId, Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rCoinsRecord.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uid SqlType(int8) */
    val uid: Rep[Long] = column[Long]("uid")
    /** Database column record_type SqlType(int4) */
    val recordType: Rep[Int] = column[Int]("record_type")
    /** Database column coins SqlType(float8) */
    val coins: Rep[Double] = column[Double]("coins")
    /** Database column remark SqlType(varchar), Length(512,true) */
    val remark: Rep[String] = column[String]("remark", O.Length(512,varying=true))
    /** Database column order_id SqlType(int8), Default(None) */
    val orderId: Rep[Option[Long]] = column[Option[Long]]("order_id", O.Default(None))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tCoinsRecord */
  lazy val tCoinsRecord = new TableQuery(tag => new tCoinsRecord(tag))

  /** Entity class storing rows of table tCoinsStandardRecord
    *  @param uid Database column uid SqlType(int8)
    *  @param oldStandard Database column old_standard SqlType(int4)
    *  @param newStandard Database column new_standard SqlType(int4)
    *  @param remark Database column remark SqlType(varchar), Length(512,true)
    *  @param createTime Database column create_time SqlType(int8) */
  case class rCoinsStandardRecord(uid: Long, oldStandard: Int, newStandard: Int, remark: String, createTime: Long)
  /** GetResult implicit for fetching rCoinsStandardRecord objects using plain SQL queries */
  implicit def GetResultrCoinsStandardRecord(implicit e0: GR[Long], e1: GR[Int], e2: GR[String]): GR[rCoinsStandardRecord] = GR{
    prs => import prs._
      rCoinsStandardRecord.tupled((<<[Long], <<[Int], <<[Int], <<[String], <<[Long]))
  }
  /** Table description of table coins_standard_record. Objects of this class serve as prototypes for rows in queries. */
  class tCoinsStandardRecord(_tableTag: Tag) extends profile.api.Table[rCoinsStandardRecord](_tableTag, "coins_standard_record") {
    def * = (uid, oldStandard, newStandard, remark, createTime) <> (rCoinsStandardRecord.tupled, rCoinsStandardRecord.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uid), Rep.Some(oldStandard), Rep.Some(newStandard), Rep.Some(remark), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rCoinsStandardRecord.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uid SqlType(int8) */
    val uid: Rep[Long] = column[Long]("uid")
    /** Database column old_standard SqlType(int4) */
    val oldStandard: Rep[Int] = column[Int]("old_standard")
    /** Database column new_standard SqlType(int4) */
    val newStandard: Rep[Int] = column[Int]("new_standard")
    /** Database column remark SqlType(varchar), Length(512,true) */
    val remark: Rep[String] = column[String]("remark", O.Length(512,varying=true))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tCoinsStandardRecord */
  lazy val tCoinsStandardRecord = new TableQuery(tag => new tCoinsStandardRecord(tag))

  /** Entity class storing rows of table tGpuEquipment
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param machineId Database column machine_id SqlType(int8)
    *  @param machineIp Database column machine_ip SqlType(varchar), Length(64,true)
    *  @param name Database column name SqlType(varchar), Length(64,true)
    *  @param fee Database column fee SqlType(int4)
    *  @param createTime Database column create_time SqlType(int8)
    *  @param memory Database column memory SqlType(int4), Default(12198) */
  case class rGpuEquipment(id: Long, machineId: Long, machineIp: String, name: String, fee: Int, createTime: Long, memory: Int = 12198)
  /** GetResult implicit for fetching rGpuEquipment objects using plain SQL queries */
  implicit def GetResultrGpuEquipment(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rGpuEquipment] = GR{
    prs => import prs._
      rGpuEquipment.tupled((<<[Long], <<[Long], <<[String], <<[String], <<[Int], <<[Long], <<[Int]))
  }
  /** Table description of table gpu_equipment. Objects of this class serve as prototypes for rows in queries. */
  class tGpuEquipment(_tableTag: Tag) extends profile.api.Table[rGpuEquipment](_tableTag, "gpu_equipment") {
    def * = (id, machineId, machineIp, name, fee, createTime, memory) <> (rGpuEquipment.tupled, rGpuEquipment.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(machineId), Rep.Some(machineIp), Rep.Some(name), Rep.Some(fee), Rep.Some(createTime), Rep.Some(memory)).shaped.<>({r=>import r._; _1.map(_=> rGpuEquipment.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column machine_id SqlType(int8) */
    val machineId: Rep[Long] = column[Long]("machine_id")
    /** Database column machine_ip SqlType(varchar), Length(64,true) */
    val machineIp: Rep[String] = column[String]("machine_ip", O.Length(64,varying=true))
    /** Database column name SqlType(varchar), Length(64,true) */
    val name: Rep[String] = column[String]("name", O.Length(64,varying=true))
    /** Database column fee SqlType(int4) */
    val fee: Rep[Int] = column[Int]("fee")
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column memory SqlType(int4), Default(12198) */
    val memory: Rep[Int] = column[Int]("memory", O.Default(12198))
  }
  /** Collection-like TableQuery object for table tGpuEquipment */
  lazy val tGpuEquipment = new TableQuery(tag => new tGpuEquipment(tag))

  /** Entity class storing rows of table tMachine
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param name Database column name SqlType(varchar), Length(128,true)
    *  @param ip Database column ip SqlType(varchar), Length(64,true)
    *  @param priority Database column priority SqlType(int4), Default(Some(1)) */
  case class rMachine(id: Long, name: String, ip: String, priority: Option[Int] = Some(1))
  /** GetResult implicit for fetching rMachine objects using plain SQL queries */
  implicit def GetResultrMachine(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[Int]]): GR[rMachine] = GR{
    prs => import prs._
      rMachine.tupled((<<[Long], <<[String], <<[String], <<?[Int]))
  }
  /** Table description of table machine. Objects of this class serve as prototypes for rows in queries. */
  class tMachine(_tableTag: Tag) extends profile.api.Table[rMachine](_tableTag, "machine") {
    def * = (id, name, ip, priority) <> (rMachine.tupled, rMachine.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(ip), priority).shaped.<>({r=>import r._; _1.map(_=> rMachine.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(128,true) */
    val name: Rep[String] = column[String]("name", O.Length(128,varying=true))
    /** Database column ip SqlType(varchar), Length(64,true) */
    val ip: Rep[String] = column[String]("ip", O.Length(64,varying=true))
    /** Database column priority SqlType(int4), Default(Some(1)) */
    val priority: Rep[Option[Int]] = column[Option[Int]]("priority", O.Default(Some(1)))
  }
  /** Collection-like TableQuery object for table tMachine */
  lazy val tMachine = new TableQuery(tag => new tMachine(tag))

  /** Entity class storing rows of table tProcessKill
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param processId Database column process_id SqlType(int8)
    *  @param linuxAccount Database column linux_account SqlType(varchar), Length(128,true)
    *  @param gpuIp Database column gpu_ip SqlType(varchar), Length(64,true)
    *  @param gpuName Database column gpu_name SqlType(varchar), Length(64,true)
    *  @param reason Database column reason SqlType(varchar), Length(512,true)
    *  @param creatTime Database column creat_time SqlType(int8) */
  case class rProcessKill(id: Long, processId: Long, linuxAccount: String, gpuIp: String, gpuName: String, reason: String, creatTime: Long)
  /** GetResult implicit for fetching rProcessKill objects using plain SQL queries */
  implicit def GetResultrProcessKill(implicit e0: GR[Long], e1: GR[String]): GR[rProcessKill] = GR{
    prs => import prs._
      rProcessKill.tupled((<<[Long], <<[Long], <<[String], <<[String], <<[String], <<[String], <<[Long]))
  }
  /** Table description of table process_kill. Objects of this class serve as prototypes for rows in queries. */
  class tProcessKill(_tableTag: Tag) extends profile.api.Table[rProcessKill](_tableTag, "process_kill") {
    def * = (id, processId, linuxAccount, gpuIp, gpuName, reason, creatTime) <> (rProcessKill.tupled, rProcessKill.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(processId), Rep.Some(linuxAccount), Rep.Some(gpuIp), Rep.Some(gpuName), Rep.Some(reason), Rep.Some(creatTime)).shaped.<>({r=>import r._; _1.map(_=> rProcessKill.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column process_id SqlType(int8) */
    val processId: Rep[Long] = column[Long]("process_id")
    /** Database column linux_account SqlType(varchar), Length(128,true) */
    val linuxAccount: Rep[String] = column[String]("linux_account", O.Length(128,varying=true))
    /** Database column gpu_ip SqlType(varchar), Length(64,true) */
    val gpuIp: Rep[String] = column[String]("gpu_ip", O.Length(64,varying=true))
    /** Database column gpu_name SqlType(varchar), Length(64,true) */
    val gpuName: Rep[String] = column[String]("gpu_name", O.Length(64,varying=true))
    /** Database column reason SqlType(varchar), Length(512,true) */
    val reason: Rep[String] = column[String]("reason", O.Length(512,varying=true))
    /** Database column creat_time SqlType(int8) */
    val creatTime: Rep[Long] = column[Long]("creat_time")
  }
  /** Collection-like TableQuery object for table tProcessKill */
  lazy val tProcessKill = new TableQuery(tag => new tProcessKill(tag))

  /** Entity class storing rows of table tRentOrder
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param userId Database column user_id SqlType(int8)
    *  @param userName Database column user_name SqlType(varchar), Length(255,true)
    *  @param equId Database column equ_id SqlType(int8)
    *  @param startTime Database column start_time SqlType(int8)
    *  @param endTime Database column end_time SqlType(int8)
    *  @param costCoins Database column cost_coins SqlType(float8)
    *  @param state Database column state SqlType(int4), Default(0)
    *  @param createTime Database column create_time SqlType(int8)
    *  @param returnCoins Database column return_coins SqlType(float8), Default(None)
    *  @param equIp Database column equ_ip SqlType(varchar), Length(255,true)
    *  @param equName Database column equ_name SqlType(varchar), Length(255,true)
    *  @param linuxAccount Database column linux_account SqlType(varchar), Length(255,true), Default()
    *  @param email Database column email SqlType(varchar), Length(255,true), Default() */
  case class rRentOrder(id: Long, userId: Long, userName: String, equId: Long, startTime: Long, endTime: Long, costCoins: Double, state: Int = 0, createTime: Long, returnCoins: Option[Double] = None, equIp: String, equName: String, linuxAccount: String = "", email: String = "")
  /** GetResult implicit for fetching rRentOrder objects using plain SQL queries */
  implicit def GetResultrRentOrder(implicit e0: GR[Long], e1: GR[String], e2: GR[Double], e3: GR[Int], e4: GR[Option[Double]]): GR[rRentOrder] = GR{
    prs => import prs._
      rRentOrder.tupled((<<[Long], <<[Long], <<[String], <<[Long], <<[Long], <<[Long], <<[Double], <<[Int], <<[Long], <<?[Double], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table rent_order. Objects of this class serve as prototypes for rows in queries. */
  class tRentOrder(_tableTag: Tag) extends profile.api.Table[rRentOrder](_tableTag, "rent_order") {
    def * = (id, userId, userName, equId, startTime, endTime, costCoins, state, createTime, returnCoins, equIp, equName, linuxAccount, email) <> (rRentOrder.tupled, rRentOrder.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(userName), Rep.Some(equId), Rep.Some(startTime), Rep.Some(endTime), Rep.Some(costCoins), Rep.Some(state), Rep.Some(createTime), returnCoins, Rep.Some(equIp), Rep.Some(equName), Rep.Some(linuxAccount), Rep.Some(email)).shaped.<>({r=>import r._; _1.map(_=> rRentOrder.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10, _11.get, _12.get, _13.get, _14.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int8) */
    val userId: Rep[Long] = column[Long]("user_id")
    /** Database column user_name SqlType(varchar), Length(255,true) */
    val userName: Rep[String] = column[String]("user_name", O.Length(255,varying=true))
    /** Database column equ_id SqlType(int8) */
    val equId: Rep[Long] = column[Long]("equ_id")
    /** Database column start_time SqlType(int8) */
    val startTime: Rep[Long] = column[Long]("start_time")
    /** Database column end_time SqlType(int8) */
    val endTime: Rep[Long] = column[Long]("end_time")
    /** Database column cost_coins SqlType(float8) */
    val costCoins: Rep[Double] = column[Double]("cost_coins")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column return_coins SqlType(float8), Default(None) */
    val returnCoins: Rep[Option[Double]] = column[Option[Double]]("return_coins", O.Default(None))
    /** Database column equ_ip SqlType(varchar), Length(255,true) */
    val equIp: Rep[String] = column[String]("equ_ip", O.Length(255,varying=true))
    /** Database column equ_name SqlType(varchar), Length(255,true) */
    val equName: Rep[String] = column[String]("equ_name", O.Length(255,varying=true))
    /** Database column linux_account SqlType(varchar), Length(255,true), Default() */
    val linuxAccount: Rep[String] = column[String]("linux_account", O.Length(255,varying=true), O.Default(""))
    /** Database column email SqlType(varchar), Length(255,true), Default() */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true), O.Default(""))
  }
  /** Collection-like TableQuery object for table tRentOrder */
  lazy val tRentOrder = new TableQuery(tag => new tRentOrder(tag))

  /** Entity class storing rows of table tRentRecord
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param userId Database column user_id SqlType(int8)
    *  @param userName Database column user_name SqlType(varchar), Length(255,true)
    *  @param equId Database column equ_id SqlType(int8)
    *  @param startTime Database column start_time SqlType(int8)
    *  @param endTime Database column end_time SqlType(int8)
    *  @param costCoins Database column cost_coins SqlType(float8)
    *  @param state Database column state SqlType(int4), Default(0)
    *  @param createTime Database column create_time SqlType(int8)
    *  @param returnCoins Database column return_coins SqlType(float8), Default(None)
    *  @param equIp Database column equ_ip SqlType(varchar), Length(255,true)
    *  @param equName Database column equ_name SqlType(varchar), Length(255,true)
    *  @param linuxAccount Database column linux_account SqlType(varchar), Length(255,true), Default()
    *  @param email Database column email SqlType(varchar), Length(255,true), Default() */
  case class rRentRecord(id: Long, userId: Long, userName: String, equId: Long, startTime: Long, endTime: Long, costCoins: Double, state: Int = 0, createTime: Long, returnCoins: Option[Double] = None, equIp: String, equName: String, linuxAccount: String = "", email: String = "")
  /** GetResult implicit for fetching rRentRecord objects using plain SQL queries */
  implicit def GetResultrRentRecord(implicit e0: GR[Long], e1: GR[String], e2: GR[Double], e3: GR[Int], e4: GR[Option[Double]]): GR[rRentRecord] = GR{
    prs => import prs._
      rRentRecord.tupled((<<[Long], <<[Long], <<[String], <<[Long], <<[Long], <<[Long], <<[Double], <<[Int], <<[Long], <<?[Double], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table rent_record. Objects of this class serve as prototypes for rows in queries. */
  class tRentRecord(_tableTag: Tag) extends profile.api.Table[rRentRecord](_tableTag, "rent_record") {
    def * = (id, userId, userName, equId, startTime, endTime, costCoins, state, createTime, returnCoins, equIp, equName, linuxAccount, email) <> (rRentRecord.tupled, rRentRecord.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(userName), Rep.Some(equId), Rep.Some(startTime), Rep.Some(endTime), Rep.Some(costCoins), Rep.Some(state), Rep.Some(createTime), returnCoins, Rep.Some(equIp), Rep.Some(equName), Rep.Some(linuxAccount), Rep.Some(email)).shaped.<>({r=>import r._; _1.map(_=> rRentRecord.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10, _11.get, _12.get, _13.get, _14.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int8) */
    val userId: Rep[Long] = column[Long]("user_id")
    /** Database column user_name SqlType(varchar), Length(255,true) */
    val userName: Rep[String] = column[String]("user_name", O.Length(255,varying=true))
    /** Database column equ_id SqlType(int8) */
    val equId: Rep[Long] = column[Long]("equ_id")
    /** Database column start_time SqlType(int8) */
    val startTime: Rep[Long] = column[Long]("start_time")
    /** Database column end_time SqlType(int8) */
    val endTime: Rep[Long] = column[Long]("end_time")
    /** Database column cost_coins SqlType(float8) */
    val costCoins: Rep[Double] = column[Double]("cost_coins")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column return_coins SqlType(float8), Default(None) */
    val returnCoins: Rep[Option[Double]] = column[Option[Double]]("return_coins", O.Default(None))
    /** Database column equ_ip SqlType(varchar), Length(255,true) */
    val equIp: Rep[String] = column[String]("equ_ip", O.Length(255,varying=true))
    /** Database column equ_name SqlType(varchar), Length(255,true) */
    val equName: Rep[String] = column[String]("equ_name", O.Length(255,varying=true))
    /** Database column linux_account SqlType(varchar), Length(255,true), Default() */
    val linuxAccount: Rep[String] = column[String]("linux_account", O.Length(255,varying=true), O.Default(""))
    /** Database column email SqlType(varchar), Length(255,true), Default() */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true), O.Default(""))
  }
  /** Collection-like TableQuery object for table tRentRecord */
  lazy val tRentRecord = new TableQuery(tag => new tRentRecord(tag))

  /** Entity class storing rows of table tUsers
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param username Database column username SqlType(varchar), Length(255,true)
    *  @param securePwd Database column secure_pwd SqlType(varchar), Length(255,true)
    *  @param email Database column email SqlType(varchar), Length(512,true)
    *  @param linuxAccount Database column linux_account SqlType(varchar), Length(128,true)
    *  @param coinStandard Database column coin_standard SqlType(int4)
    *  @param createTime Database column create_time SqlType(int8)
    *  @param state Database column state SqlType(int4), Default(0)
    *  @param coin Database column coin SqlType(float8), Default(0.0) */
  case class rUsers(id: Long, username: String, securePwd: String, email: String, linuxAccount: String, coinStandard: Int, createTime: Long, state: Int = 0, coin: Double = 0.0)
  /** GetResult implicit for fetching rUsers objects using plain SQL queries */
  implicit def GetResultrUsers(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[Double]): GR[rUsers] = GR{
    prs => import prs._
      rUsers.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Long], <<[Int], <<[Double]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class tUsers(_tableTag: Tag) extends profile.api.Table[rUsers](_tableTag, "users") {
    def * = (id, username, securePwd, email, linuxAccount, coinStandard, createTime, state, coin) <> (rUsers.tupled, rUsers.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(username), Rep.Some(securePwd), Rep.Some(email), Rep.Some(linuxAccount), Rep.Some(coinStandard), Rep.Some(createTime), Rep.Some(state), Rep.Some(coin)).shaped.<>({r=>import r._; _1.map(_=> rUsers.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255,varying=true))
    /** Database column secure_pwd SqlType(varchar), Length(255,true) */
    val securePwd: Rep[String] = column[String]("secure_pwd", O.Length(255,varying=true))
    /** Database column email SqlType(varchar), Length(512,true) */
    val email: Rep[String] = column[String]("email", O.Length(512,varying=true))
    /** Database column linux_account SqlType(varchar), Length(128,true) */
    val linuxAccount: Rep[String] = column[String]("linux_account", O.Length(128,varying=true))
    /** Database column coin_standard SqlType(int4) */
    val coinStandard: Rep[Int] = column[Int]("coin_standard")
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column coin SqlType(float8), Default(0.0) */
    val coin: Rep[Double] = column[Double]("coin", O.Default(0.0))
  }
  /** Collection-like TableQuery object for table tUsers */
  lazy val tUsers = new TableQuery(tag => new tUsers(tag))
}
