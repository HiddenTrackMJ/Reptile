package com.neo.sk.utils

import com.neo.sk.reptile.core.proxy.ProxyInfo
import org.slf4j.LoggerFactory
import com.neo.sk.reptile.Boot.executor
import io.circe.generic.auto._
import io.circe.parser.decode


/**
  * User: Jason
  * Date: 2019/3/20
  * Time: 17:08
  */


object ProxyClient extends HttpUtil {

  private val log = LoggerFactory.getLogger(this.getClass)

  final case class GetProxyResonseData(
    count:Int,
    proxy_list:List[String]
  )

  final case class GetProxyResponse(msg:String,
    code:Int,
    data:GetProxyResonseData
  )

  /**
    * 获取代理数据
    * */
  def getProxy(url:String) = {
    val mehtodName = s"getProxy"
    getRequestSend(mehtodName,url,Nil).map{
      case Right(result) =>
        decodeProxyResponse(result)
      case Left(error) =>
        log.debug(s"$mehtodName get request failed,error:${error.getMessage}")
        Left("$mehtodName get request failed,error:${error.getMessage}")
    }
  }

  private def decodeProxyResponse(jsonString:String) = {
    decode[GetProxyResponse](jsonString) match {
      case Right(rsp) =>
        val proxyData = rsp.data.proxy_list.map{
          proxy =>
            val t = proxy.split(":")
            ProxyInfo(t(0),t(1))
        }
        Right(proxyData)
      case Left(error) =>
        log.debug(s"decode proxy response failure,when json string=$jsonString,error:${error.getMessage}")
        Left(s"decode proxy response failure,when json string=$jsonString,error:${error.getMessage}")
    }
  }

  def main(args: Array[String]): Unit = {
    getProxy("http://dev.kuaidaili.com/api/getproxy/?orderid=920034333687984&num=100&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=2&method=2&an_an=1&an_ha=1&sp1=1&quality=1&sort=1&format=json&sep=1")
  }

}
