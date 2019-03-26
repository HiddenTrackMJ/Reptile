package com.neo.sk.reptile.core.Increment

import com.neo.sk.reptile.models.dao.ArticleDAO.getLatesTime
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success}
import com.neo.sk.reptile.Boot.executor
/**
  * User: Jason
  * Date: 2019/3/4
  * Time: 15:25
  */
class IncrementByTime extends Increment {

  private val log = LoggerFactory.getLogger(this.getClass)

  private var latestArticleTime = 0L


  override def initial(): Unit = {
    getLatesTime.onComplete{
      case Success(t) =>
        if (t.isDefined) {
          latestArticleTime = t.get
        }
        else {
          log.warn(s"get latest article time error!, time is $t")
        }
      case Failure(e) =>
        log.warn(s"There are no recent articles! $e")
    }
  }

  override def update(s: String): Unit = {
    latestArticleTime = s.toLong
  }

  override def isNew(s: String): Boolean = {
    if(s.toLong > latestArticleTime)
      true
    else
      false
  }

}
