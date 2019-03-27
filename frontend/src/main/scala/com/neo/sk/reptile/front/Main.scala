package com.neo.sk.reptile.front

import cats.Show
import com.neo.sk.reptile.front.pages._
import mhtml.mount
import org.scalajs.dom
import com.neo.sk.reptile.front.utils.{Http, JsFunc, PageSwitcher}
import mhtml._
import org.scalajs.dom
import io.circe.syntax._
import io.circe.generic.auto._
import com.neo.sk.reptile.front.styles.{ListStyles, LoginStyles}
/**
  * Created by haoshuhan on 2018/6/4.
  */
object Main extends PageSwitcher {
  val currentPage = currentHashVar.map { ls =>
    println(s"currentPage change to ${ls.mkString(",")}")
    ls match {
      case "Login" :: Nil => Login.app
      case _ => Login.app
    }

  }

  def show(): Cancelable = {
    switchPageByHash()
    val page =
      <div>
        {currentPage}
      </div>
    mount(dom.document.body, page)
  }


  def main(args: Array[String]): Unit ={
    import scalacss.ProdDefaults._
    LoginStyles.addToDocument()
    ListStyles.addToDocument()
    show()
  }
}
