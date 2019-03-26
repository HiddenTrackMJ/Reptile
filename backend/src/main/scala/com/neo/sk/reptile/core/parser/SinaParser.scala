package com.neo.sk.reptile.core.parser

import akka.actor.typed.ActorRef
import com.neo.sk.reptile.core.spider.{SpiderRst, SpiderTaskError}
import com.neo.sk.reptile.core.spider
import com.neo.sk.reptile.core.task
import com.neo.sk.reptile.core.task.Task
import com.neo.sk.reptile.models
import com.neo.sk.reptile.models._

/**
  * User: Jason
  * Date: 2019/2/28
  * Time: 11:00
  * 新浪新闻解析
  */
class SinaParser(app:NewsApp) extends Parser {

  override def parseArticle(rst: SpiderRst): Either[spider.SpiderTaskError, Article] = ???

  override def parseColumn(rst: SpiderRst): Either[spider.SpiderTaskError, List[task.Task]] = ???

  override def parseComment(rst: SpiderRst): Either[spider.SpiderTaskError,
    (List[Either[spider.SpiderTaskError, Comment]], List[Task])] = ???
}
