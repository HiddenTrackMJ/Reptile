package com.neo.sk.reptile.front.common

/**
  * User: Jason
  * Date: 2018/8/17
  * Time: 13:55
  */
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLElement
trait Component[T <: HTMLElement] {
  def render: T



}

trait Page extends Component[Div] {

  private[this] var selfDom: Div = _

  def locationHash: String

  def mounted(): Unit = {}

  def unMounted(): Unit = {}

  def get: Div = {
    if(selfDom == null){
      selfDom = render
    }
    selfDom
  }

}