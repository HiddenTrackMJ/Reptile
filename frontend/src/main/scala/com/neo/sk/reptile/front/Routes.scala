package com.neo.sk.reptile.front

/**
  * Created by haoshuhan on 2018/6/4.
  */
object Routes {
  val base = "/todos2018"

  object User {
    val baseUrl: String = base + "/user"
    val login: String = baseUrl + "/userLogin"
    val logout: String = baseUrl + "/userLogout"
    val addUser: String = baseUrl + "/addUser"
  }

  object List {
    val baseUrl: String = base + "/list"
    val getList: String = baseUrl + "/getList"
    val addRecord: String = baseUrl + "/addRecord"
    val delRecord: String = baseUrl + "/delRecord"
    val crawler: String = baseUrl + "/crawler"
    val getCrawlA: String = baseUrl + "/getCrawlA"
    val getFailedTasks: String = baseUrl + "/getFailedTasks"
    val getCateA: String = baseUrl + "/getCateA"
    val ToPage: String = baseUrl + "/ToPage"
    val CateAmountA: String = baseUrl + "/CateAmountA"
    val Search: String = baseUrl + "/Search"
    val ToPageS: String = baseUrl + "/ToPageS"
  }
  object Comment {
    val baseUrl: String = base + "/Comment"
    val getCrawlC: String = baseUrl + "/getCrawlC"
    val commentcrawler: String = baseUrl + "/commentcrawler"
    val Stopcommentcrawler: String = baseUrl + "/Stopcommentcrawler"
//    val getFailedTasks = baseUrl + "/getFailedTasks"
    val SeetheArticle: String = baseUrl + "/SeeTheArticle"
    val SeetheComments: String = baseUrl + "/SeeTheComments"
    val ToPageC: String = baseUrl + "/ToPageC"
    val ToPageD: String = baseUrl + "/ToPageD"
    val CommentAmount: String = baseUrl + "/CommentAmount"
    val FailedTaskAmount: String = baseUrl + "/FailedTaskAmount"
    val Reply: String = baseUrl + "/Reply"
    val Search: String = baseUrl + "/Search"
    val FailedTaskChecker: String = baseUrl + "/FailedTaskChecker"
  }

}
