package com.atguigu.realtime.bean

case class AlertInfo(mid: String,
                     uids: java.util.HashSet[String],
                     itemIds: java.util.HashSet[String],
                     events: java.util.HashSet[String],
                     ts: Long)
