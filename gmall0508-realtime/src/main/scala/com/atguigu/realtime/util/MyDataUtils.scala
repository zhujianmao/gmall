package com.atguigu.realtime.util


import java.text.SimpleDateFormat
import java.util.Date

object MyDataUtils {

  val dayFormater: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val minFormater: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
  val hourFormater: SimpleDateFormat = new SimpleDateFormat("HH")

  def getMinuteDayAndMonAndYear(time:Long): String ={
    minFormater.format(new Date(time))
  }

  def getYearMonDay(time: Long): String = {
    dayFormater.format(new Date(time))
  }

  def getHour(time: Long): String = {
    hourFormater.format(new Date(time))
  }

}
