package com.neo.sk.reptile.common

import com.neo.sk.reptile.core.spider

/**
  * User: Jason
  * Date: 2019/3/4
  * Time: 15:28
  */
object Constant {
  object TaskPriority{
    val column = 10
    val article = 5
    val image = 1

    def genTaskPriorityByType(taskType:spider.TaskType.Value) = {
      taskType match {
        case spider.TaskType.columnPage =>
          TaskPriority.column
        case spider.TaskType.articlePage =>
          TaskPriority.article
        case _ =>
          TaskPriority.image
      }
    }
  }


  object SpiderTaskErrorCode{
    val hestiaImageUploadError = -10
  }
}
