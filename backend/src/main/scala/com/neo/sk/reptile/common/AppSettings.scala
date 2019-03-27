package com.neo.sk.reptile.common

import java.util.concurrent.TimeUnit

import com.neo.sk.utils.SessionSupport.SessionConfig
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable
import com.neo.sk.reptile.models._


/**
  * User: Taoz
  * Date: 9/4/2015
  * Time: 4:29 PM
  */
object AppSettings {

  private implicit class RichConfig(config: Config) {
    val noneValue = "none"

    def getOptionalString(path: String): Option[String] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getString(path))

    def getOptionalLong(path: String): Option[Long] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getLong(path))

    def getOptionalDurationSeconds(path: String): Option[Long] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getDuration(path, TimeUnit.SECONDS))
  }


  val log: Logger = LoggerFactory.getLogger(this.getClass)
  val config: Config = ConfigFactory.parseResources("product.conf").withFallback(ConfigFactory.load())

  val appConfig: Config = config.getConfig("app")

  val httpInterface: String = appConfig.getString("http.interface")
  val httpPort: Int = appConfig.getInt("http.port")


  val slickConfig: Config = config.getConfig("slick.db")
  val slickUrl: String = slickConfig.getString("url")
  val slickUser: String = slickConfig.getString("user")
  val slickPassword: String = slickConfig.getString("password")
  val slickMaximumPoolSize: Int = slickConfig.getInt("maximumPoolSize")
  val slickConnectTimeout: Int = slickConfig.getInt("connectTimeout")
  val slickIdleTimeout: Int = slickConfig.getInt("idleTimeout")
  val slickMaxLifetime: Int = slickConfig.getInt("maxLifetime")



  val sessionConfig: SessionConfig = {
    val sConf = config.getConfig("session")
    SessionConfig(
      cookieName = sConf.getString("cookie.name"),
      serverSecret = sConf.getString("serverSecret"),
      domain = sConf.getOptionalString("cookie.domain"),
      path = sConf.getOptionalString("cookie.path"),
      secure = sConf.getBoolean("cookie.secure"),
      httpOnly = sConf.getBoolean("cookie.httpOnly"),
      maxAge = sConf.getOptionalDurationSeconds("cookie.maxAge"),
      sessionEncryptData = sConf.getBoolean("encryptData")
    )


  }



  val appSecureMap: Map[String, String] = {
    val appIds = appConfig.getStringList("client.appIds").asScala
    val secureKeys = appConfig.getStringList("client.secureKeys").asScala
    require(appIds.length == secureKeys.length, "appIdList.length and secureKeys.length not equel.")
    appIds.zip(secureKeys).toMap
  }

  object proxyConf {
    private val proxyConfig = config.getConfig("proxy")
    lazy val isWork: Boolean = proxyConfig.getBoolean("isWork")
    lazy val proxyFetchUrl: String = proxyConfig.getString("proxyFetchUrl")
  }

  object SpiderConf {
    private val cf = config.getConfig("spider")
    val isWork: Boolean = cf.getBoolean("isWork")
    val spiderNum: Int = cf.getInt("spiderNum")
    val idleInterval: Long = cf.getLong("spiderIdleInterval")
  }

  object NewsAppConf{
    private val cf = config.getConfig("newsApp")

    val duplicatedFilterTime: Long = cf.getLong("duplicatedFilterTime") * 24 * 60 * 60 * 1000

    val newsAppMap = new mutable.HashMap[Int,NewsApp]()

    object sina{
      private val appCf = cf.getConfig("sina")
      private val id = appCf.getInt("id")
      private val name = appCf.getString("name")
      private val nameCn = appCf.getString("nameCn")
      private val columnParseCode = appCf.getString("columnParseCode")
      private val articleParseCode = appCf.getString("articleParseCode")
      private val columnCf = appCf.getConfig("column")

      private val sport = columnCf.getString("sports")
      private val society = columnCf.getString("society")
      private val military = columnCf.getString("military")
      private val science = columnCf.getString("science")
      private val international = columnCf.getString("international")
      private val entertainment = columnCf.getString("entertainment")
      private val finance = columnCf.getString("finance")
      private val newsApp = NewsApp(id,name,nameCn,List(
        NewsAppColumn("sport","体育",sport),
        NewsAppColumn("society","社会",society),
        NewsAppColumn("military","军事",military),
        NewsAppColumn("science","科技",science),
        NewsAppColumn("international","国际",international),
        NewsAppColumn("entertainment","娱乐",entertainment),
        NewsAppColumn("finance","财经",finance)
      ),appCf.getString("increment"),appCf.getBoolean("useProxy"),columnParseCode,articleParseCode)
      newsAppMap.put(id,newsApp)
    }

    object tencent{
      private val appCf = cf.getConfig("tencent")
      private val id = appCf.getInt("id")
      private val name = appCf.getString("name")
      private val nameCn = appCf.getString("nameCn")
      private val columnParseCode = appCf.getString("columnParseCode")
      private val articleParseCode = appCf.getString("articleParseCode")
      private val columnCf = appCf.getConfig("column")

      private val sport = columnCf.getString("sports")
      private val society = columnCf.getString("society")
      private val military = columnCf.getString("military")
      private val science = columnCf.getString("science")
      private val international = columnCf.getString("international")
      private val entertainment = columnCf.getString("entertainment")
      private val finance = columnCf.getString("finance")
      private val car = columnCf.getString("car")
      private val culture = columnCf.getString("culture")
      private val education = columnCf.getString("education")

      private val newsApp = NewsApp(id,name,nameCn,List(
        NewsAppColumn("sport","体育",sport),
        NewsAppColumn("society","社会",society),
        NewsAppColumn("military","军事",military),
        NewsAppColumn("science","科技",science),
        NewsAppColumn("international","国际",international),
        NewsAppColumn("entertainment","娱乐",entertainment),
        NewsAppColumn("finance","财经",finance),
        NewsAppColumn("car","汽车",car),
        NewsAppColumn("culture","文化",culture),
        NewsAppColumn("education","教育",education)
      ),appCf.getString("increment"),appCf.getBoolean("useProxy"),columnParseCode,articleParseCode)
      newsAppMap.put(id,newsApp)
    }

    object ntes{
      private val appCf = cf.getConfig("netEase")
      private val id = appCf.getInt("id")
      private val name = appCf.getString("name")
      private val nameCn = appCf.getString("nameCn")
      private val columnParseCode = appCf.getString("columnParseCode")
      private val articleParseCode = appCf.getString("articleParseCode")
      private val columnCf = appCf.getConfig("column")

      private val sport = columnCf.getString("sports")
      private val society = columnCf.getString("society")
      private val military = columnCf.getString("military")
      private val science = columnCf.getString("science")
      private val international = columnCf.getString("international")
      private val entertainment = columnCf.getString("entertainment")
      private val finance = columnCf.getString("finance")
      private val car = columnCf.getString("car")
      private val jiangkang = columnCf.getString("jiangkang")
      private val education = columnCf.getString("education")

      private val newsApp = NewsApp(id,name,nameCn,List(
        NewsAppColumn("sport","体育",sport),
        NewsAppColumn("society","社会",society),
        NewsAppColumn("military","军事",military),
        NewsAppColumn("science","科技",science),
        NewsAppColumn("international","国际",international),
        NewsAppColumn("entertainment","娱乐",entertainment),
        NewsAppColumn("finance","财经",finance),
        NewsAppColumn("car","汽车",car),
        NewsAppColumn("jiangkang","健康",jiangkang),
        NewsAppColumn("education","教育",education)
      ),appCf.getString("increment"),appCf.getBoolean("useProxy"),columnParseCode,articleParseCode)
      newsAppMap.put(id,newsApp)
    }

    def init() :Unit = {
//      sina
//      tencent
      ntes
    }
  }

}
