package com.neo.sk.reptile.core.parser

import com.neo.sk.reptile.core.{spider, task}
import com.neo.sk.reptile.models.{Article, Comment, NewsApp}

/**
  * User: Jason
  * Date: 2019/2/28
  * Time: 10:59
  * 网易新闻解析
  */
class NetEaseParser(app:NewsApp) extends Parser {

  override def parseArticle: Either[spider.SpiderTaskError, Article] = ???

  override def parseColumn: Either[spider.SpiderTaskError, List[task.Task]] = ???

  override def parseComment: Either[spider.SpiderTaskError, Comment] = ???
}
