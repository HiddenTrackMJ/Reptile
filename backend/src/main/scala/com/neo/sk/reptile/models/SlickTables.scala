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
  lazy val schema: profile.SchemaDescription = tArticle.schema ++ tComment.schema ++ tSpiderFailedTask.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tArticle
    *  @param aid Database column aid SqlType(bigserial), AutoInc, PrimaryKey
    *  @param appId Database column app_id SqlType(int4)
    *  @param appName Database column app_name SqlType(varchar), Length(32,true), Default()
    *  @param appNameCn Database column app_name_cn SqlType(varchar), Length(32,true), Default()
    *  @param columnName Database column column_name SqlType(varchar), Length(32,true), Default()
    *  @param columnNameCn Database column column_name_cn SqlType(varchar), Length(32,true), Default()
    *  @param title Database column title SqlType(text), Default()
    *  @param content Database column content SqlType(text), Default()
    *  @param html Database column html SqlType(text), Default()
    *  @param postTime Database column post_time SqlType(int8), Default(0)
    *  @param src Database column src SqlType(varchar), Length(32,true), Default(None)
    *  @param author Database column author SqlType(varchar), Length(32,true), Default(None)
    *  @param srcImage Database column src_image SqlType(text), Default(None)
    *  @param srcUrl Database column src_url SqlType(varchar), Length(128,true), Default() */
  case class rArticle(aid: Long, appId: Int, appName: String = "", appNameCn: String = "", columnName: String = "", columnNameCn: String = "", title: String = "", content: String = "", html: String = "", postTime: Long = 0L, src: Option[String] = None, author: Option[String] = None, srcImage: Option[String] = None, srcUrl: String = "")
  /** GetResult implicit for fetching rArticle objects using plain SQL queries */
  implicit def GetResultrArticle(implicit e0: GR[Long], e1: GR[Int], e2: GR[String], e3: GR[Option[String]]): GR[rArticle] = GR{
    prs => import prs._
      rArticle.tupled((<<[Long], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Long], <<?[String], <<?[String], <<?[String], <<[String]))
  }
  /** Table description of table article. Objects of this class serve as prototypes for rows in queries. */
  class tArticle(_tableTag: Tag) extends profile.api.Table[rArticle](_tableTag, "article".toUpperCase) {
    def * = (aid, appId, appName, appNameCn, columnName, columnNameCn, title, content, html, postTime, src, author, srcImage, srcUrl) <> (rArticle.tupled, rArticle.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(aid), Rep.Some(appId), Rep.Some(appName), Rep.Some(appNameCn), Rep.Some(columnName), Rep.Some(columnNameCn), Rep.Some(title), Rep.Some(content), Rep.Some(html), Rep.Some(postTime), src, author, srcImage, Rep.Some(srcUrl)).shaped.<>({r=>import r._; _1.map(_=> rArticle.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11, _12, _13, _14.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column aid SqlType(bigserial), AutoInc, PrimaryKey */
    val aid: Rep[Long] = column[Long]("aid".toUpperCase, O.AutoInc, O.PrimaryKey)
    /** Database column app_id SqlType(int4) */
    val appId: Rep[Int] = column[Int]("app_id".toUpperCase)
    /** Database column app_name SqlType(varchar), Length(32,true), Default() */
    val appName: Rep[String] = column[String]("app_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column app_name_cn SqlType(varchar), Length(32,true), Default() */
    val appNameCn: Rep[String] = column[String]("app_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column column_name SqlType(varchar), Length(32,true), Default() */
    val columnName: Rep[String] = column[String]("column_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column column_name_cn SqlType(varchar), Length(32,true), Default() */
    val columnNameCn: Rep[String] = column[String]("column_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column title SqlType(text), Default() */
    val title: Rep[String] = column[String]("title".toUpperCase, O.Default(""))
    /** Database column content SqlType(text), Default() */
    val content: Rep[String] = column[String]("content".toUpperCase, O.Default(""))
    /** Database column html SqlType(text), Default() */
    val html: Rep[String] = column[String]("html".toUpperCase, O.Default(""))
    /** Database column post_time SqlType(int8), Default(0) */
    val postTime: Rep[Long] = column[Long]("post_time".toUpperCase, O.Default(0L))
    /** Database column src SqlType(varchar), Length(32,true), Default(None) */
    val src: Rep[Option[String]] = column[Option[String]]("src".toUpperCase, O.Length(32,varying=true), O.Default(None))
    /** Database column author SqlType(varchar), Length(32,true), Default(None) */
    val author: Rep[Option[String]] = column[Option[String]]("author".toUpperCase, O.Length(32,varying=true), O.Default(None))
    /** Database column src_image SqlType(text), Default(None) */
    val srcImage: Rep[Option[String]] = column[Option[String]]("src_image".toUpperCase, O.Default(None))
    /** Database column src_url SqlType(varchar), Length(128,true), Default() */
    val srcUrl: Rep[String] = column[String]("src_url".toUpperCase, O.Length(128,varying=true), O.Default(""))
  }
  /** Collection-like TableQuery object for table tArticle */
  lazy val tArticle = new TableQuery(tag => new tArticle(tag))

  /** Entity class storing rows of table tComment
    *  @param cid Database column cid SqlType(bigserial), AutoInc, PrimaryKey
    *  @param appId Database column app_id SqlType(int4)
    *  @param appName Database column app_name SqlType(varchar), Length(32,true), Default()
    *  @param appNameCn Database column app_name_cn SqlType(varchar), Length(32,true), Default()
    *  @param columnName Database column column_name SqlType(varchar), Length(32,true), Default()
    *  @param columnNameCn Database column column_name_cn SqlType(varchar), Length(32,true), Default()
    *  @param content Database column content SqlType(text), Default()
    *  @param posttime Database column posttime SqlType(int8), Default(0)
    *  @param source Database column source SqlType(text), Default()
    *  @param user Database column user SqlType(varchar), Length(128,true), Default(None)
    *  @param userid Database column userid SqlType(int8), Default(None)
    *  @param imagelist Database column imagelist SqlType(text), Default(None)
    *  @param articleurl Database column articleurl SqlType(varchar), Length(128,true), Default()
    *  @param commenturl Database column commenturl SqlType(varchar), Length(128,true), Default()
    *  @param replyid Database column replyid SqlType(int8), Default(None)
    *  @param commentid Database column commentid SqlType(int8), Default(0)
    *  @param buildlevel Database column buildlevel SqlType(int4), Default(1)
    *  @param vote Database column vote SqlType(int4), Default(0) */
  case class rComment(cid: Long, appId: Int, appName: String = "", appNameCn: String = "", columnName: String = "", columnNameCn: String = "", content: String = "", posttime: Long = 0L, source: String = "", user: Option[String] = None, userid: Option[Long] = None, imagelist: Option[String] = None, articleurl: String = "", commenturl: String = "", replyid: Option[Long] = None, commentid: Long = 0L, buildlevel: Int = 1, vote: Int = 0)
  /** GetResult implicit for fetching rComment objects using plain SQL queries */
  implicit def GetResultrComment(implicit e0: GR[Long], e1: GR[Int], e2: GR[String], e3: GR[Option[String]], e4: GR[Option[Long]]): GR[rComment] = GR{
    prs => import prs._
      rComment.tupled((<<[Long], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Long], <<[String], <<?[String], <<?[Long], <<?[String], <<[String], <<[String], <<?[Long], <<[Long], <<[Int], <<[Int]))
  }
  /** Table description of table comment. Objects of this class serve as prototypes for rows in queries. */
  class tComment(_tableTag: Tag) extends profile.api.Table[rComment](_tableTag, "comment".toUpperCase) {
    def * = (cid, appId, appName, appNameCn, columnName, columnNameCn, content, postTime, source, user, userId, imageList, articleUrl, commentUrl, replyId, commentId, buildLevel, vote) <> (rComment.tupled, rComment.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(cid), Rep.Some(appId), Rep.Some(appName), Rep.Some(appNameCn), Rep.Some(columnName), Rep.Some(columnNameCn), Rep.Some(content), Rep.Some(postTime), Rep.Some(source), user, userId, imageList, Rep.Some(articleUrl), Rep.Some(commentUrl), replyId, Rep.Some(commentId), Rep.Some(buildLevel), Rep.Some(vote)).shaped.<>({r=>import r._; _1.map(_=> rComment.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10, _11, _12, _13.get, _14.get, _15, _16.get, _17.get, _18.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column cid SqlType(bigserial), AutoInc, PrimaryKey */
    val cid: Rep[Long] = column[Long]("cid".toUpperCase, O.AutoInc, O.PrimaryKey)
    /** Database column app_id SqlType(int4) */
    val appId: Rep[Int] = column[Int]("app_id".toUpperCase)
    /** Database column app_name SqlType(varchar), Length(32,true), Default() */
    val appName: Rep[String] = column[String]("app_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column app_name_cn SqlType(varchar), Length(32,true), Default() */
    val appNameCn: Rep[String] = column[String]("app_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column column_name SqlType(varchar), Length(32,true), Default() */
    val columnName: Rep[String] = column[String]("column_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column column_name_cn SqlType(varchar), Length(32,true), Default() */
    val columnNameCn: Rep[String] = column[String]("column_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column content SqlType(text), Default() */
    val content: Rep[String] = column[String]("content".toUpperCase, O.Default(""))
    /** Database column posttime SqlType(int8), Default(0) */
    val postTime: Rep[Long] = column[Long]("posttime".toUpperCase, O.Default(0L))
    /** Database column source SqlType(text), Default() */
    val source: Rep[String] = column[String]("source".toUpperCase, O.Default(""))
    /** Database column user SqlType(varchar), Length(128,true), Default(None) */
    val user: Rep[Option[String]] = column[Option[String]]("user".toUpperCase, O.Length(128,varying=true), O.Default(None))
    /** Database column userid SqlType(int8), Default(None) */
    val userId: Rep[Option[Long]] = column[Option[Long]]("userid".toUpperCase, O.Default(None))
    /** Database column imagelist SqlType(text), Default(None) */
    val imageList: Rep[Option[String]] = column[Option[String]]("imagelist".toUpperCase, O.Default(None))
    /** Database column articleurl SqlType(varchar), Length(128,true), Default() */
    val articleUrl: Rep[String] = column[String]("articleurl".toUpperCase, O.Length(128,varying=true), O.Default(""))
    /** Database column commenturl SqlType(varchar), Length(128,true), Default() */
    val commentUrl: Rep[String] = column[String]("commenturl".toUpperCase, O.Length(128,varying=true), O.Default(""))
    /** Database column replyid SqlType(int8), Default(None) */
    val replyId: Rep[Option[Long]] = column[Option[Long]]("replyid".toUpperCase, O.Default(None))
    /** Database column commentid SqlType(int8), Default(0) */
    val commentId: Rep[Long] = column[Long]("commentid".toUpperCase, O.Default(0L))
    /** Database column buildlevel SqlType(int4), Default(1) */
    val buildLevel: Rep[Int] = column[Int]("buildlevel".toUpperCase, O.Default(1))
    /** Database column vote SqlType(int4), Default(0) */
    val vote: Rep[Int] = column[Int]("vote".toUpperCase, O.Default(0))
  }
  /** Collection-like TableQuery object for table tComment */
  lazy val tComment = new TableQuery(tag => new tComment(tag))

  /** Entity class storing rows of table tSpiderFailedTask
    *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
    *  @param appId Database column app_id SqlType(int4)
    *  @param appName Database column app_name SqlType(varchar), Length(32,true), Default()
    *  @param appNameCn Database column app_name_cn SqlType(varchar), Length(32,true), Default()
    *  @param url Database column url SqlType(varchar), Length(128,true), Default()
    *  @param taskType Database column task_type SqlType(varchar), Length(16,true), Default()
    *  @param error Database column error SqlType(text), Default()
    *  @param createTime Database column create_time SqlType(int8), Default(0) */
  case class rSpiderFailedTask(id: Long, appId: Int, appName: String = "", appNameCn: String = "", url: String = "", taskType: String = "", error: String = "", createTime: Long = 0L)
  /** GetResult implicit for fetching rSpiderFailedTask objects using plain SQL queries */
  implicit def GetResultrSpiderFailedTask(implicit e0: GR[Long], e1: GR[Int], e2: GR[String]): GR[rSpiderFailedTask] = GR{
    prs => import prs._
      rSpiderFailedTask.tupled((<<[Long], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Long]))
  }
  /** Table description of table spider_failed_task. Objects of this class serve as prototypes for rows in queries. */
  class tSpiderFailedTask(_tableTag: Tag) extends profile.api.Table[rSpiderFailedTask](_tableTag, "spider_failed_task") {
    def * = (id, appId, appName, appNameCn, url, taskType, error, createTime) <> (rSpiderFailedTask.tupled, rSpiderFailedTask.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(appId), Rep.Some(appName), Rep.Some(appNameCn), Rep.Some(url), Rep.Some(taskType), Rep.Some(error), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rSpiderFailedTask.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id".toUpperCase, O.AutoInc, O.PrimaryKey)
    /** Database column app_id SqlType(int4) */
    val appId: Rep[Int] = column[Int]("app_id".toUpperCase)
    /** Database column app_name SqlType(varchar), Length(32,true), Default() */
    val appName: Rep[String] = column[String]("app_name".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column app_name_cn SqlType(varchar), Length(32,true), Default() */
    val appNameCn: Rep[String] = column[String]("app_name_cn".toUpperCase, O.Length(32,varying=true), O.Default(""))
    /** Database column url SqlType(varchar), Length(128,true), Default() */
    val url: Rep[String] = column[String]("url".toUpperCase, O.Length(128,varying=true), O.Default(""))
    /** Database column task_type SqlType(varchar), Length(16,true), Default() */
    val taskType: Rep[String] = column[String]("task_type".toUpperCase, O.Length(16,varying=true), O.Default(""))
    /** Database column error SqlType(text), Default() */
    val error: Rep[String] = column[String]("error".toUpperCase, O.Default(""))
    /** Database column create_time SqlType(int8), Default(0) */
    val createTime: Rep[Long] = column[Long]("create_time".toUpperCase, O.Default(0L))
  }
  /** Collection-like TableQuery object for table tSpiderFailedTask */
  lazy val tSpiderFailedTask = new TableQuery(tag => new tSpiderFailedTask(tag))
}
