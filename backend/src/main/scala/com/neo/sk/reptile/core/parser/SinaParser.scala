package com.neo.sk.reptile.core.parser

import akka.actor.typed.ActorRef
import com.neo.sk.reptile.core.{spider, task}
import com.neo.sk.reptile.models._

/**
  * User: Jason
  * Date: 2019/2/28
  * Time: 11:00
  * 新浪新闻解析
  */
class SinaParser(app:NewsApp) extends Parser {

  override def parseArticle: Either[spider.SpiderTaskError, Article] = ???

  override def parseColumn: Either[spider.SpiderTaskError, List[task.Task]] = ???

  override def parseComment: Either[spider.SpiderTaskError, Comment] = ???
}
