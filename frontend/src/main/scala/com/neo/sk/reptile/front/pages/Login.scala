package com.neo.sk.reptile.front.pages

import com.neo.sk.reptile.front.utils.{Http, JsFunc}
import com.neo.sk.reptile.front.{Page, Routes}
import com.neo.sk.reptile.shared.ptcl.SuccessRsp
import org.scalajs.dom
import org.scalajs.dom.html.Input
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.reptile.front.styles.LoginStyles._
/**
  * Created by haoshuhan on 2018/6/4.
  */
object Login extends Page{


  def app: xml.Node = {
    <div>
      <div>
        <div class={welcome.htmlClass}>欢迎登录</div>
      </div>
      <div>
        <div class={container.htmlClass}>用户名：<input class={input.htmlClass} id="username"></input>
        </div>
        <div class={container.htmlClass}>密  码：<input type="password" class={input.htmlClass} id="password"></input>
        </div>
      </div>
      <button class={button.htmlClass} >登录</button>
      <div class={container.htmlClass}> <a href="http://www.baidu.com">没有账号？请在下面注册？</a></div>
        <div>
          <div class={welcome.htmlClass}>欢迎注册</div>
        </div>
        <div>
          <div class={container.htmlClass}>用户名：<input class={input.htmlClass} id="username1"></input>
          </div>
          <div class={container.htmlClass}>密  码：<input type="password" class={input.htmlClass} id="password1"></input>
          </div>
        </div>
        <button class={button.htmlClass} >注册</button>
    </div>

  }
}
