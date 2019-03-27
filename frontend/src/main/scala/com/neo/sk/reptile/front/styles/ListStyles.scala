package com.neo.sk.reptile.front.styles
import scala.language.postfixOps
import scalacss.DevDefaults._

/**
  * Created by haoshuhan on 2018/6/4.
  */
object ListStyles extends StyleSheet.Inline{
  import dsl._
  val ff = fontFace("myFont")(
    _.src("url(font.woff)")
      .fontStretch.expanded
      .fontStyle.italic
      .unicodeRange(0, 5))
  val ff2 = fontFace("myFont2")(
    _.src("url(font2.woff)")
      .fontStyle.oblique
      .fontWeight._200)
  val ff3 = fontFace("myFont3")(
    _.src("local(HelveticaNeue)", "url(font2.woff)")
      .fontStretch.ultraCondensed
      .fontWeight._200)
  val ff4 = fontFace("myFont3")(_.src("local(HelveticaNeue)", "url(font2.woff)"))
  val ff5 = fontFace(_.src("local(HelveticaNeue)", "url(font2.woff)"))
  val ff6 = fontFace(_.src("local(HelveticaNeue)", "url(font.woff)"))

  val common = mixin(
    backgroundColor.lightskyblue
  )
  val th = style(
    width(160 px),
    backgroundColor.lightgreen
  )
  val th1 = style(
    width(500 px),
    backgroundColor.lightgreen
  )
  val td = style(
    width(160 px),

  )
  val td1 = style(
    width(500 px),
  )
  val input = style(
    width(250 px),
    height(45 px),
    borderRadius(5 px),
    fontSize(17 px)
  )

  val addButton = style(
    width(100 px),
    common,
    height(38 px),
    borderRadius(5 px),
    fontSize(17 px),
    &.hover(
      cursor.zoomIn
    ),

    media.not.handheld.landscape.maxWidth(640 px)(
      width(400 px)
    )
  )

  val deleteButton = style(
    addClassNames("btn", "btn-default"),
    width(60 px),
    height(30 px),
    borderRadius(5 px),
    fontSize(15 px),
    &.hover(
      cursor.zoomIn
    ),

    media.not.handheld.landscape.maxWidth(640 px)(
      width(400 px)
    )

  )

  val logoutButton = style(float.right, marginRight(5.%%))
  val left1 = style(float.left, marginLeft(5.%%))
  val left2 = style(
    marginLeft(10.%%)
  )
  val left3 = style(
    marginLeft(13.%%)
  )

  val commentL = style(
    width(1000 px),
    position.relative,
    margin.auto,
    borderRadius(0.3 px),
    borderCollapse.collapse,
    borderBottom(0.2 px) ,
    borderBottomColor.white,
    backgroundColor.lightskyblue,
//    borderBottomWidth(5 px),
    borderBottom.dashed

  )
  val commentL2 = style(
    width(900 px),
    position.relative,
    margin.auto,
//    left(100 px),
//    borderBottomWidth(1 px),
    borderCollapse.collapse,
    backgroundColor.lightgray,
    borderBottom(0.2 px) ,
    borderBottomColor.white,
    borderBottom.solid

  )
  val commentID = style(
    left(10.%%),
//    fontFamily  ,
    fontSize(19 px),
    float.left
  )
  val commentTime = style(
    textAlign.right,
    right(10.%%)
  )
  val commentContent = style(
    margin.auto
  )
  val commentButton = style(
    textAlign.right
  )
  val display2 = style(
    width(250 px),
    overflow.hidden,
    whiteSpace.nowrap,
    float.right,
    backgroundColor.lightskyblue,
    display.none
  )
  val display1 = style(
    position.fixed,
    bottom(20 px),
    right(0 px),
    backgroundColor.lightskyblue,
    &.hover(
      unsafeChild(s".${display2.htmlClass}")(
        display.inlineBlock,

      )
    )
  )

  val display3 = style(
    width(10 px),
    height(400 px),
    backgroundColor.lightskyblue,
    display.inlineBlock
  )

  val display5 = style(
    width(250 px),
    overflow.hidden,
    whiteSpace.nowrap,
    float.left,
    backgroundColor.lightskyblue,
    display.none
  )
  val display4 = style(
    position.fixed,
    bottom(120 px),
    left(0 px),
    backgroundColor.lightskyblue,
    &.hover(
      unsafeChild(s".${display5.htmlClass}")(
        display.inlineBlock,
      )
    )
  )

  val display6 = style(
    width(10 px),
    height(45 px),
    backgroundColor.lightskyblue,
    display.inlineBlock
  )
  val display8 = style(
    width(400 px),
    overflow.hidden,
    whiteSpace.nowrap,
    float.right,
    backgroundColor.lightskyblue,
    display.none
  )
  val display7 = style(
    position.fixed,
    bottom(300 px),
    right(0 px),
    backgroundColor.lightskyblue,
    &.hover(
      unsafeChild(s".${display8.htmlClass}")(
        display.inlineBlock,
      )
    )
  )

  val display9 = style(
    width(10 px),
    height(50 px),
    backgroundColor.lightskyblue,
    display.inlineBlock
  )
  val top = style(
    position.fixed,
    bottom(20 px),
    left(20 px),
    backgroundColor.white,
    width(80 px),
    height(100 px)
  )
}
