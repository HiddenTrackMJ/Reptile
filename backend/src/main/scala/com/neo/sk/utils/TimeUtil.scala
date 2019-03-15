package com.neo.sk.utils

import java.text.SimpleDateFormat

/**
  * User: Jason
  * Date: 2019/3/1
  * Time: 16:55
  */
object TimeUtil {

  import com.github.nscala_time.time.Imports._

  def sinaDate2TimeStamp(date: String): Long = {
    new SimpleDateFormat("yyyy年MM月dd日 HH:mm").parse(date).getTime
  }

  def ntesDate2TimeStamp(date: String): Long = {
    new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").parse(date).getTime
  }

  def date2TimeStampMMddHHss(date:String):Long = {
    try{
      val d = date.split(" ")(0)
      val t = date.split(" ")(1)
      new DateTime().withMonthOfYear(d.split("-")(0).toInt).withDayOfMonth(d.split("-")(1).toInt)
        .withHourOfDay(t.split(":")(0).toInt).withMinuteOfHour(t.split(":")(1).toInt)
        .withSecondOfMinute(0).withMillisOfSecond(0).getMillis
    }catch {
      case e:Exception =>
        throw e
    }

  }

  def dateMMddYY2TimeStamp(date:String):Long = {
    new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(date).getTime

  }

  def dateYYMMdd2TimeStamp(date:String):Long = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date).getTime

  }

  def main(args: Array[String]): Unit = {
    println(date2TimeStampMMddHHss("2-19 13:57"))
  }

}
