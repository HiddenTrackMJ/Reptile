package com.neo.sk.reptile.shared.ptcl

/**
  * Created by hongruying on 2018/2/21
  */
object AccountProtocol {

  import com.neo.sk.reptile.shared.ptcl._

  case class LoginReq(
                     accountName:String,
                     password:String
                     )
  case class CrawlReq(

  )
  def LoginErrorRsp(msg:String,code:Int = 1000500) = ErrorRsp(code,msg)
  def RegisterErrorRsp(msg:String,code:Int = 1000500) = ErrorRsp(code,msg)
  def CrawlErrorRsp(code:Int = 1000500,msg:String)=ErrorRsp(code,msg)


  case class GetUserInfoRsp(
                        data:Option[model.AccountInfo],
                        errCode: Int = 0,
                        msg: String = "ok"
                        ) extends CommonRsp




}
