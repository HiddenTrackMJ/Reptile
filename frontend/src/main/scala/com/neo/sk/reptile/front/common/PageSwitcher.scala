package com.neo.sk.reptile.front.common

/**
  * User: Jason
  * Date: 2018/8/17
  * Time: 13:54
  */
import com.neo.sk.reptile.front.Page
import org.scalajs.dom
import org.scalajs.dom.HashChangeEvent
trait PageSwitcher {

  import scalatags.JsDom.short._

  private var currentPage: Page = _

  private val bodyContent = div(*.height := "100%").render

  def getCurrentHash: String = dom.window.location.hash


  private[this] var internalTargetHash = ""


  //init.
  {

    val func = {
      e: HashChangeEvent =>
        //only handler browser history hash changed.
        if (internalTargetHash != getCurrentHash) {
          println(s"hash changed, new hash: $getCurrentHash")
          switchPageByHash()
        }
    }
    dom.window.addEventListener("hashchange", func, useCapture = false)

    /*    dom.window.onhashchange = { _: HashChangeEvent =>
          //only handler browser history hash changed.
          if (internalTargetHash != getCurrentHash) {
            println(s"hash changed, new hash: $getCurrentHash")
            switchPageByHash()
          }
        }*/

    dom.document.body.appendChild(bodyContent)
  }


  protected def switchToPage(page: Page): Unit = {
    if (currentPage != null) {
      currentPage.unMounted()
    }
    println(s"switchPage from [$getCurrentHash] to [${page.locationHash}]")
    currentPage = page
    bodyContent.textContent = ""
    if (getCurrentHash != page.locationHash) {
      dom.window.location.hash = page.locationHash
      internalTargetHash = page.locationHash
    }
    bodyContent.appendChild(page.get)
    currentPage.mounted()
  }

  def getCurrentPage: Page = currentPage

  def switchPageByHash(): Unit

}
