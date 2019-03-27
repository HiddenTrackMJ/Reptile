package com.neo.sk.utils

import com.neo.sk.utils.DBUtil.driver
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.H2Profile

/**
 * User: Taoz
 * Date: 2/9/2015
 * Time: 4:33 PM
 */
object DBUtil {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val dataSource = createDataSource()

  import com.neo.sk.reptile.common.AppSettings._

  private def createDataSource() = {

//    val dataSource = new org.postgresql.ds.PGSimpleDataSource()
    val dataSource = new org.h2.jdbcx.JdbcDataSource

    //val dataSource = new MysqlDataSource()

    log.info(s"connect to db: $slickUrl")
    dataSource.setUrl(slickUrl)
    dataSource.setUser(slickUser)
    dataSource.setPassword(slickPassword)

    val hikariDS = new HikariDataSource()
    hikariDS.setDataSource(dataSource)
    hikariDS.setMaximumPoolSize(slickMaximumPoolSize)
    hikariDS.setConnectionTimeout(slickConnectTimeout)
    hikariDS.setIdleTimeout(slickIdleTimeout)
    hikariDS.setMaxLifetime(slickMaxLifetime)
    hikariDS.setAutoCommit(true)
    hikariDS
  }

   val driver = H2Profile

  import driver.api.Database

  val db: driver.backend.DatabaseDef = Database.forDataSource(dataSource, Some(slickMaximumPoolSize))




}