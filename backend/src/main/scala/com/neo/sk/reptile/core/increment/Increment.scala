package com.neo.sk.reptile.core.increment

/**
  * User: Jason
  * Date: 2019/3/4
  * Time: 15:25
  */
trait Increment {

  def initial():Unit
  //更新增量
  def update(s:String):Unit

  //判断是否是新的数据
  def isNew(s:String):Boolean

}
