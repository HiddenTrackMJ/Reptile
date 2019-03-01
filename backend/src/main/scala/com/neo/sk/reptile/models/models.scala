package com.neo.sk.reptile

/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 10:01
  */
package object models {
  case class NewsAppColumn(
    name:String,
    nameCn:String,
    url:String
  )

  case class NewsApp(
    id:Int,
    name:String,
    nameCn:String,
    column:List[NewsAppColumn],
    increment:String,
    useProxy:Boolean,
    columnParseCode:String = "utf-8",
    articleParseCode:String = "utf-8",
  )


  case class Article(
    appId:Int,
    appName:String,
    appNameCn:String,
    columnName:String,
    columnNameCn:String,
    title:String,
    content:String,
    html:String,
    postTime:Long,
    from:Option[String],
    author:Option[String],
    imageList:Option[List[ArticleImage]],
    url:String
  )

  case class Comment(
    appId:Int,
    appName:String,
    appNameCn:String,
    columnName:String,
    columnNameCn:String,
    articleTitle:String,
    content:String,
    postTime:Long,
    from:Option[String],
    user:Option[String],
    imageList:Option[List[ArticleImage]],
    url:String
  )

  case class ArticleImage(
    imageSrcUrl:String,
    imageName:Option[String]
  )
}
