package com.neo.sk.reptile.core.parser

import akka.actor.typed.ActorRef
import com.neo.sk.reptile.core.increment.Increment
import com.neo.sk.reptile.core.spider.{SpiderRst, SpiderTaskError}
import com.neo.sk.reptile.core.spider
import com.neo.sk.reptile.core.task
import com.neo.sk.reptile.core.task.Task
import com.neo.sk.reptile.models.{Article, Comment, NewsApp, NewsAppColumn}

/**
  * User: Jason
  * Date: 2019/2/28
  * Time: 10:59
  * 腾讯新闻解析
  */
class TencentParser(app:NewsApp, newsAppColumn:NewsAppColumn, wrapper:ActorRef[spider.SpiderRst], increment:Increment) extends Parser {

  override def parseArticle(rst: SpiderRst): Either[spider.SpiderTaskError, Article] = ???

  override def parseColumn(rst: SpiderRst): Either[spider.SpiderTaskError, List[task.Task]] = ???

  override def parseComment(rst: SpiderRst): Either[spider.SpiderTaskError,
    (List[Either[spider.SpiderTaskError, Comment]], List[Task])] = ???
}
