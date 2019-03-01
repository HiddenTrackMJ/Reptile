package com.neo.sk.reptile.shared

/**
  * User: Taoz
  * Date: 5/30/2017
  * Time: 10:37 AM
  */
/**
  *
  * Created by liuziwei on 2017/5/5.
  *
  */


package object ptcl {

  trait CommonRsp {
    val errCode: Int
    val msg: String
  }

  final case class ErrorRsp(
    errCode: Int,
    msg: String
  ) extends CommonRsp

  final case class SuccessRsp(
    errCode: Int = 0,
    msg: String = "ok"
  ) extends CommonRsp


  object model{
    case class AccountInfo(aId:Long,accountName:String,loginTime:Long)

    case class ColumnInfo(columnName:String,columnNameCn:String)

    case class CommentInfo(
                          cId:Long,
                          accountId:Long,
                          accountName:String,
                          articleId:Long,
                          replyId:Option[Long],
                          content:String,
                          createTime:Long
                          )


    case class ArticleInfo(
                          aId:Long,
                          appId:Long,
                          appName:String,
                          appNameCn:String,
                          columnName:String,
                          columnNameCn:String,
                          title:String,
                          content:String,
                          postTime:Long,
                          src:Option[String],
                          author:Option[String],
                          src_img:Option[String],
                          src_url:String,

                          commentCount:Int,
                          thumbUpCount:Int
                          )
  }

  object CommonErrorCode{
    val jsonFormatError = ErrorRsp(msg="json parse error.",errCode=1000001)
    val noSessionError = ErrorRsp(msg="no session,need to login",errCode = 1000002)
    val articleNoExitError = ErrorRsp(msg="article no exit",errCode = 1000003)
    val accountNoExitError = ErrorRsp(msg="account no exit",errCode = 1000004)
    val passwordError = ErrorRsp(msg="account no exit",errCode = 1000005)
    val accountExitError = ErrorRsp(msg="account is exixt",errCode = 1000006)

  }


}
