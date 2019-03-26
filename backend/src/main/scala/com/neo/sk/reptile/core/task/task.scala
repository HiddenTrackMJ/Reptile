package com.neo.sk.reptile.core

import akka.actor.typed.ActorRef
import com.neo.sk.reptile.core.spider.{SpiderCommand, SpiderTask}

/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 11:51
  */
package object task {

  trait TaskCommand

  final case class Task(task:SpiderTask,
    priority: Int = 0,
    needProxy:Boolean = false) extends TaskCommand

  final case class FetchTask(replyTo:ActorRef[SpiderCommand]) extends TaskCommand

}
