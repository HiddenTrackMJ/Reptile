package com.neo.sk.reptile.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.reptile.common.Constant.TaskPriority
import com.neo.sk.reptile.core.Increment.{Increment, IncrementByTime}
import com.neo.sk.reptile.core.parser.Parser
import com.neo.sk.reptile.core.spider
import com.neo.sk.reptile.core.task.Task
import com.neo.sk.reptile.core.spider._
import com.neo.sk.reptile.models.{Article, NewsApp, NewsAppColumn, SlickTables}
import com.neo.sk.reptile.models.dao.{ArticleDAO, rArticle}
import org.slf4j.LoggerFactory
import com.neo.sk.reptile.Boot.executor

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * User: Jason
  * Date: 2019/3/26
  * Time: 17:45
  */




object Column {

  private val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command

  final case object StartWork extends Command

  final case object Work extends Command

  final case class ChildDead(name:String) extends Command

  final case class SwitchBehavior(name:String,behavior:Behavior[Command]) extends Command

  final case class TimeoutWhenBusy(msg:String) extends Command

  final case class MySpiderRst(task: SpiderTask, rst: Either[SpiderTaskError, SpiderTaskSuccess]) extends Command

  final case object FinishWork extends Command

  private case object NextWorkKey
  private val nextWorkTime = 5.minutes

  private case object InitTimeKey
  private val initTime = 5.minutes


  
  def create(spiderManager:ActorRef[SpiderManager.Command],
    storeActor:ActorRef[StoreActor.Command],
    appName:NewsApp,
    newsAppColumn:NewsAppColumn,
    useProxy:Boolean,
    increment:String) :Behavior[Command] = {
    Behaviors.setup[Command]{ ctx =>
      log.debug(s"${ctx.self.path} is starting")
      Behaviors.withTimers[Command]{ timer =>

        ctx.self ! StartWork
        timer.startSingleTimer(InitTimeKey,TimeoutWhenBusy("init"),initTime)
//        init(ctx,spiderManager,storeActor,appName,newsAppColumn,useProxy,increment,timer)
        busy(Nil)
      }
    }
  }


  private def idle(
    spiderManager:ActorRef[SpiderManager.Command],
    storeActor:ActorRef[StoreActor.Command],
    appName:NewsApp,
    newsAppColumn:NewsAppColumn,
    useProxy:Boolean,
    increment: Increment,
    parser: Parser,
    wrapper: ActorRef[spider.SpiderRst],
    timer:TimerScheduler[Command]
  ):Behavior[Command] = {
    Behaviors.receive[Command]{ (ctx,msg) =>

      msg match {
        case StartWork =>
          log.debug(s"${ctx.self.path} is time to start work")
          Behaviors.same
//          startWork(ctx,spiderManager,storeActor,appName,newsAppColumn,useProxy,increment,parser,wrapper,timer)


        case unknow =>
          log.error(s"${ctx.self.path} receive an unknown msg=${msg}")
          Behaviors.same
      }
    }
  }


//  private def work(
//    spiderManager:ActorRef[SpiderManager.Command],
//    storeActor:ActorRef[StoreActor.Command],
//    app:NewsApp,
//    newsAppColumn:NewsAppColumn,
//    useProxy:Boolean,
//    increment: Increment,
//    parser: Parser,
//    wrapper: ActorRef[spider.SpiderRst],
//    unDispatchTaskList:List[Task],
//    unFinishTaskList:List[Task],
//    articleList:List[rArticle],
//    imageList:List[SlickTables.rArticleImage],
//    timer:TimerScheduler[Command],
//    cmdStash:List[Command]
//  ) :Behavior[Command] = {
//    Behaviors.receive{ (ctx,msg) =>
//
//      def dispatchAllTask(unDispatchTask:List[Task],
//        unFinishTask:List[Task],
//        article:List[SlickTables.rArticle] =Nil,
//        image:List[SlickTables.rArticleImage] = Nil
//      ):Behavior[Command] = {
//
//        unDispatchTask.foreach(spiderManager ! SpiderManager.AddTask(_))
//        work(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,increment,parser,wrapper,Nil,unDispatchTask ::: unFinishTask,articleList:::article,imageList:::image,timer,cmdStash)
//      }
//
//      def genImageTask(article:Article):List[Task] = {
//        article.imageList.getOrElse(Nil).map{i =>
//          Task(SpiderTask(i.imageSrcUrl,TaskType.image,None,None,wrapper,maxTryTime = 1),TaskPriority.image,false)//不适用proxy
//        }
//      }
//
//      def genArticle(article:Article):SlickTables.rArticle = {
//        SlickTables.rArticle(-1L,app.id,app.name,app.nameCn,newsAppColumn.name,newsAppColumn.nameCn,article.title,
//          article.content,article.html,article.postTime,article.from,article.author,article.imageList.map(t => t.map{x =>
//            if(x.imageName.nonEmpty)
//              s"${x.imageSrcUrl}--${x.imageName.get}"
//            else
//              s"${x.imageSrcUrl}"
//          }.mkString("##")),article.url)
//      }
//
//      def dealSpiderRst(myRst:MySpiderRst):Behavior[Command] = {
//        val curUnFinishTaskList = unFinishTaskList.filterNot(_.task.url == myRst.task.url)
//        myRst.task.taskType match {
//          case spider.TaskType.columnPage =>
//            parser.parserColumnPage(myRst) match {
//              case Right(taskList) =>
//                val columnPageTask = taskList.filter(_.task.taskType == TaskType.columnPage)
//                val articlePageTask = taskList.filterNot(_.task.taskType == TaskType.columnPage)
//                if(columnPageTask.nonEmpty){
//                  columnPageTask.foreach(spiderManager ! SpiderManager.AddTask(_))
//                  work(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,increment,parser,wrapper,articlePageTask:::unDispatchTaskList,
//                    columnPageTask:::curUnFinishTaskList,articleList,imageList,timer,cmdStash)
//                }else{
//                  val curUnDispatchTaskList = articlePageTask:::unDispatchTaskList
//                  if(curUnDispatchTaskList.nonEmpty){
//                    dispatchAllTask(curUnDispatchTaskList,curUnFinishTaskList)
//                  }else{
//                    if(curUnFinishTaskList.isEmpty)
//                      ctx.self ! FinishWork
//                    work(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,increment,parser,wrapper,curUnDispatchTaskList,curUnFinishTaskList,articleList,imageList,timer,cmdStash)
//                  }
//                }
//
//
//              case Left(e) =>
//                log.warn(s"${ctx.self.path} parse task=${myRst.task}, code=${e.code} failed,error=${e.entity}")
//                dealFailedTask(storeActor,app,myRst.task,e.entity)
//                if(unDispatchTaskList.nonEmpty){
//                  dispatchAllTask(unDispatchTaskList,curUnFinishTaskList)
//                }else{
//                  if(curUnFinishTaskList.isEmpty)
//                    ctx.self ! FinishWork
//                  work(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,increment,parser,wrapper,unDispatchTaskList,curUnFinishTaskList,articleList,imageList,timer,cmdStash)
//                }
//            }
//
//          case spider.TaskType.articlePage =>
//            parser.parserArticlePage(myRst) match {
//              case Right(article) =>
//                val imageTask = genImageTask(article)
//                //                val imageTask = Nil
//                val curUnDispatch = imageTask ::: unDispatchTaskList
//                if(curUnDispatch.nonEmpty){
//                  dispatchAllTask(curUnDispatch,curUnFinishTaskList,List(genArticle(article)))
//                }else{
//                  if(curUnFinishTaskList.isEmpty)
//                    ctx.self ! FinishWork
//                  work(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,increment,parser,wrapper,curUnDispatch,curUnFinishTaskList,genArticle(article) :: articleList,imageList,timer,cmdStash)
//                }
//
//              case Left(e) =>
//                log.warn(s"${ctx.self.path} parse task=${myRst.task}, code=${e.code} failed,error=${e.entity}")
//                dealFailedTask(storeActor,app,myRst.task,e.entity)
//                if(unDispatchTaskList.nonEmpty){
//                  dispatchAllTask(unDispatchTaskList,curUnFinishTaskList)
//                }else{
//                  if(curUnFinishTaskList.isEmpty)
//                    ctx.self ! FinishWork
//                  work(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,increment,parser,wrapper,unDispatchTaskList,curUnFinishTaskList,articleList,imageList,timer,cmdStash)
//                }
//            }
//
//          case spider.TaskType.image =>
//            myRst.rst match {
//              case Right(r) =>
//                val image = SlickTables.rArticleImage(myRst.task.url,r.entity)
//                if(unDispatchTaskList.nonEmpty){
//                  dispatchAllTask(unDispatchTaskList,curUnFinishTaskList,image = List(image))
//                }else{
//                  if(curUnFinishTaskList.isEmpty)
//                    ctx.self ! FinishWork
//                  work(spiderManager,storeActor,app,newsAppColumn,useProxy,increment,parser,wrapper,unDispatchTaskList,curUnFinishTaskList,articleList,image::imageList,timer,cmdStash)
//                }
//
//              case Left(e) =>
//                log.warn(s"${ctx.self.path} parse task=${myRst.task}, code=${e.code} failed,error=${e.entity}")
//                dealFailedTask(storeActor,app,myRst.task,e.entity)
//                if(unDispatchTaskList.nonEmpty){
//                  dispatchAllTask(unDispatchTaskList,curUnFinishTaskList)
//                }else{
//                  if(curUnFinishTaskList.isEmpty)
//                    ctx.self ! FinishWork
//                  work(spiderManager,storeActor,app,newsAppColumn,useProxy,increment,parser,wrapper,unDispatchTaskList,curUnFinishTaskList,articleList,imageList,timer,cmdStash)
//                }
//            }
//        }
//      }
//
//      msg match {
//        case r@MySpiderRst(t,rst) =>
//          dealSpiderRst(r)
//
//
//        case FinishWork =>
//          log.debug(s"${ctx.self} recv a msg=${msg}")
//          saveArticle(storeActor,articleList)
//          updateIncrement(app,increment,articleList)
//          timer.startSingleTimer(NextWorkKey,StartWork,nextWorkTime)
//          cmdStash.reverse.foreach(ctx.self ! _)
//          idle(spiderManager,storeActor,app,newsAppColumn,useProxy,increment,parser,wrapper,timer)
//
//
//        case unknow =>
//          work(spiderManager,storeActor,app,newsAppColumn,useProxy,increment,parser,wrapper,unDispatchTaskList,unFinishTaskList,articleList,imageList,timer,msg :: cmdStash)
//      }
//    }
//  }
//
  private def busy(cmdStash:List[Command]) :Behavior[Command] = {
    Behaviors.receive{ (ctx,msg) =>
      msg match {
        case SwitchBehavior(name,behavior) =>
          log.debug(s"${ctx.self.path} change behavior to $name")

          cmdStash.reverse.foreach(ctx.self ! _)
          behavior

        case TimeoutWhenBusy(m) =>
          log.warn(s"$m time out")
          Behaviors.stopped

        case unknow =>
          busy(msg::cmdStash)
      }

    }
  }
//
//  private def init(ctx:ActorContext[Command],
//    spiderManager:ActorRef[SpiderManager.Command],
//    storeActor:ActorRef[StoreActor.Command],
//    app:NewsApp,
//    newsAppColumn:NewsAppColumn,
//    useProxy:Boolean,
//    increment:String,
//    timer:TimerScheduler[Command]) = {
//
//    val wrapper = ctx.spawnAdapter {
//      r: spider.SpiderRst ⇒ MySpiderRst(r.task, r.rst)
//    }
//    if (increment == "time"){
//      ArticleDAO.getLastestArticle(app.id,column = newsAppColumn.name).onComplete{
//        case Success(latestTimeOpt) =>
//          val lastestTime = latestTimeOpt.getOrElse(1517414400000L)
//          val incremnet = new IncrementByTime
//          incremnet.update(lastestTime.toString)
//          val parser:Parser = Parser.apply(app,newsAppColumn,wrapper,incremnet)
//          timer.cancel(InitTimeKey)
//          ctx.self ! SwitchBehavior("idle",idle(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,incremnet,parser,wrapper,timer))
//
//        case Failure(error) =>
//          log.error(s"${ctx.self.path} get lastest article failed,error=${error.getMessage}")
//      }
//    }else{
//      val increment = new IncrementByTime
//      val parser:Parser = Parser.apply(app,newsAppColumn,wrapper,increment)
//      timer.cancel(InitTimeKey)
//      ctx.self ! SwitchBehavior("idle",idle(spiderManager,storeActor,duplicatedFilter,app,newsAppColumn,useProxy,increment,parser,wrapper,timer))
//    }
//  }
//
//  private def startWork(ctx:ActorContext[Command],
//    spiderManager:ActorRef[SpiderManager.Command],
//    storeActor:ActorRef[StoreActor.Command],
//    app:NewsApp,
//    newsAppColumn:NewsAppColumn,
//    useProxy:Boolean,
//    increment: Increment,
//    parser: Parser,
//    wrapper: ActorRef[spider.SpiderRst],
//    timer:TimerScheduler[Command]):Behavior[Command] = {
//    val firstTask = SpiderTask(newsAppColumn.url,spider.TaskType.columnPage,None,None,wrapper,code = app.columnParseCode,headerOpt = spider.spiderHeader.buildHeader(app.name))
//    spiderManager ! SpiderManager.AddTask(Task(firstTask,TaskPriority.column,useProxy))
//    work(spiderManager,storeActor,app,newsAppColumn,useProxy,increment,parser,wrapper,Nil,List(Task(firstTask,TaskPriority.column,useProxy)),Nil,Nil,timer,Nil)
//  }
//
//  private def dealFailedTask(
//    storeActor:ActorRef[StoreActor.Command],
//    app:NewsApp,
//    task:SpiderTask,
//    error:String
//  ):Unit = {
//    log.debug(s"task=${task} failed")
//    val taskType = task.taskType match {
//      case TaskType.columnPage => "column"
//      case TaskType.articlePage => "article"
//      case TaskType.image => "image"
//    }
//    storeActor ! StoreActor.StoreSpiderTaskFailed(SlickTables.rSpiderFailedTask(
//      -1L,app.id,app.name,app.nameCn,task.url,taskType,error,System.currentTimeMillis()
//    ))
//  }
//
//  private def saveArticle(
//    storeActor:ActorRef[StoreActor.Command],
//    artileList:List[rArticle],
//  ) = {
//    if(artileList.nonEmpty)
//      storeActor ! StoreActor.StoreArticleList(artileList.sortBy(_.postTime))
////    if(imageList.nonEmpty)
////      storeActor ! StoreActor.StoreArticleImage(imageList)
//  }
//
//  private def updateIncrement(
//    app:NewsApp,
//    increment: Increment,
//    artileList:List[rArticle]
//  ) ={
//    if(increment.isInstanceOf[IncrementByTime]){
//      if(artileList.nonEmpty)
//        increment.update(artileList.map(_.postTime).max.toString)
//    }
//  }
}
