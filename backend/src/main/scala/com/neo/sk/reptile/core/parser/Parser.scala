package com.neo.sk.reptile.core.parser

import akka.actor.typed.ActorRef
import org.slf4j.LoggerFactory
import com.neo.sk.reptile.core.spider._
import com.neo.sk.reptile.core.spider
import com.neo.sk.reptile.core.task._
import com.neo.sk.reptile.core.parser._
import com.neo.sk.reptile.models._


/**
  * User: Jason
  * Date: 2019/2/28
  * Time: 10:51
  * 解析网页工具
  */
object Parser {

//  def apply(app:NewsApp,
//    newsAppColumn:NewsAppColumn,
//    wrapper:ActorRef[spider.SpiderRst], increment:Increment): Parser = {
//    app.name match {
//      case "sina" => new SinaParser(app,newsAppColumn,wrapper,increment)
//      case "tencent" => new TencentParser(app,newsAppColumn,wrapper,increment)
//      case "netEase" => new NetEaseParser(app,newsAppColumn,wrapper,increment)
//      case _ => null
//    }
//  }

}

trait Parser {
  def parseColumn: Either[SpiderTaskError, List[Task]]

  def parseArticle: Either[SpiderTaskError, Article]

  def parseComment: Either[SpiderTaskError, Comment]
}
