package com.neo.sk.reptile.shared.ptcl

/**
  * Created by hongruying on 2018/2/20
  */
object ArticleProtocol {

  import com.neo.sk.reptile.shared.ptcl._
  import com.neo.sk.reptile.shared.ptcl.model._

  object ColumnProtocol{

    case class AddDefineColumnReq(
                             columnName:String,
                             columnNameCn:String
                           )

    case class DeleteDefColumnReq(
                                   columnName:String,
                                   columnNameCn:String
                                 )

    case class GetColumnRsp(
                           useColumn:Option[List[ColumnInfo]],
                           noUseColumn:Option[List[ColumnInfo]],
                           errCode: Int = 0,
                           msg: String = "ok"
                           ) extends CommonRsp


    def GetColumnErrorRsp(msg:String,code:Int = 1000100) = ErrorRsp(code,msg)
    def AddColumnErrorRsp(msg:String,code:Int = 1000101) = ErrorRsp(code,msg)


  }

  object CommentProtocol{
    case class CommentArticleReq(
                         articleId:Long,
                         replyId:Option[Long],
                         content:String
                         )

    case class GetCommentReq(
                            articleId:Long
                            )

    case class GetCommentRsp(
                            comments:Option[List[CommentInfo]],
                            errCode: Int = 0,
                            msg: String = "ok"
                            ) extends CommonRsp

    def GetCommentErrorRsp(msg:String,code:Int = 1000200) = ErrorRsp(code,msg)
    def CommentArticleErrorRsp(msg:String,code:Int = 1000201) = ErrorRsp(code,msg)
  }


  object ThumbUpProtocol{
    case class ThumbUpReq(
                            articleId:Long
                            )

    case class GetThumbUpRsp(
                            data:Option[Boolean],
                            errCode: Int = 0,
                            msg: String = "ok"
                            ) extends CommonRsp




    def ThumbUpErrorRsp(msg:String,code:Int = 1000300) = ErrorRsp(code,msg)
    def CancelThumbUpErrorRsp(msg:String,code:Int = 1000301) = ErrorRsp(code,msg)
  }

  case class GetArticleListByColumnReq(
                                     columnName:String
                                     )

  case class GetArticleListByColumnRsp(
                                        articles:Option[List[ArticleInfo]],
                                        errCode: Int = 0,
                                        msg: String = "ok"
                                      ) extends CommonRsp

  case class GetArticleDetailRsp(
                                  article:Option[ArticleInfo],
                                  errCode: Int = 0,
                                  msg: String = "ok"
                                ) extends CommonRsp

  def GetArticleListByColumnErrorRsp(msg:String,code:Int = 1000300) = ErrorRsp(code,msg)
  def GetArticleDetailErrorRsp(msg:String,code:Int = 1000301) = ErrorRsp(code,msg)


}
