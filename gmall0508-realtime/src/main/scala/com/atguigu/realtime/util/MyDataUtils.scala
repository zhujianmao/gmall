package com.atguigu.realtime.util


import java.text.SimpleDateFormat
import java.util.Date

object MyDataUtil {

  val dayFormater: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val hourFormater: SimpleDateFormat = new SimpleDateFormat("HH")

  def getYearMonDay(time: Long): String = {
    dayFormater.format(new Date().setTime(time))
  }

  def getHour(time: Long): String = {
    hourFormater.format(new Date().setTime(time))
  }

}
