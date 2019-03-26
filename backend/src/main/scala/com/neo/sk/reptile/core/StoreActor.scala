package com.neo.sk.reptile.core

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl._
//import com.neo.sk.reptile.core.spider.SpiderTask
import com.neo.sk.reptile.models.dao._
//import com.neo.sk.reptile.models.dao.{ArticleDAO, SpiderTaskDAO}
import org.slf4j.LoggerFactory
import com.neo.sk.reptile.Boot.executor

import scala.util.{Failure, Success}

/**
  * User: Jason
  * Date: 2019/3/26
  * Time: 17:02
  */


object StoreActor {

  private val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command

  final case class StoreArticleList(article:Iterable[rArticle]) extends Command

//  final case class StoreArticleImage(image:Iterable[rArticleImage]) extends Command
//
//  final case class StoreSpiderTaskFailed(t:rSpiderFailedTask) extends Command
//
//  final case class StoreArticleDuplicated(rs:Iterable[rArticleduplicatedrecord]) extends Command

  private final case class SwitchBehavior(name:String,behavior: Behavior[Command]) extends Command

  def create():Behavior[Command] = {
    log.debug(s"store actor is starting...")
    Behaviors.setup[Command]{
      ctx =>
        work
    }
  }

  def work:Behavior[Command] = {
    Behaviors.receive[Command]{ (ctx,msg) =>
      msg match {
        case StoreArticleList(article) =>
          ArticleDAO.addArticle(article).onComplete{
            case Success(_) =>
              ctx.self ! SwitchBehavior("working",work)
            case Failure(e) =>
              log.warn(s"${ctx.self.path} add article failed,article=$article,error=${e.getMessage}")
              ctx.self ! SwitchBehavior("work",work)
          }
          busy(Nil)

//        case StoreArticleImage(a) =>
//          ArticleDAO.insertArticleImage(a).onComplete{
//            case Success(_) =>
//              ctx.self ! SwitchBehavior("working",working)
//            case Failure(e) =>
//              log.warn(s"${ctx.self.path} add article image failed,image=${a},error=${e.getMessage}")
//              ctx.self ! SwitchBehavior("working",working)
//          }
//          busy(Nil)
//
//        case StoreSpiderTaskFailed(t) =>
//          SpiderTaskDAO.addTaskFailed(t).onComplete{
//            case Success(_) =>
//              ctx.self ! SwitchBehavior("working",working)
//            case Failure(e) =>
//              log.warn(s"${ctx.self.path} inser spider task failed,task=${t},error=${e.getMessage}")
//              ctx.self ! SwitchBehavior("working",working)
//          }
//          busy(Nil)
//
//        case StoreArticleDuplicated(rs) =>
//          ArticleDAO.insertArticleDuplicatedRecord(rs).onComplete{
//            case Success(_) =>
//              ctx.self ! SwitchBehavior("working",working)
//            case Failure(e) =>
//              log.warn(s"${ctx.self.path} insert article duplicated record failed,records=${rs},error=${e.getMessage}")
//              ctx.self ! SwitchBehavior("working",working)
//          }
//          busy(Nil)

        case unknow =>
          log.warn(s"${ctx.self.path} recv an unknown msg=$msg")
          Behaviors.same
      }
    }
  }

  def busy(cmdStash:List[Command]): Behavior[Command] ={
    Behaviors.receive[Command]{(ctx,msg) =>
      msg match{
        case SwitchBehavior(name,behavior) =>
          log.info(s"${ctx.self.path} become $name")
          cmdStash.reverse.foreach(ctx.self ! _)
          behavior
        case _ =>
          busy(msg::cmdStash)
      }
    }
  }
}
