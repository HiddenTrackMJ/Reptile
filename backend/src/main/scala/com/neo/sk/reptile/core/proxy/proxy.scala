package com.neo.sk.reptile.core

/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 13:31
  */
package object proxy {

  case class ProxyInfo(ip:String, port:String)

  case class ProxyRsp(proxy:Option[ProxyInfo])

}