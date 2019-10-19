package com.atguigu.realtime.app

import java.util

import com.alibaba.fastjson.JSON
import com.atguigu.dw.gmall.mock.constant.ConstantUtil
import com.atguigu.dw.gmall.mock.util.ElasticSearchUtils
import com.atguigu.realtime.bean.{AlertInfo, EventLog}
import com.atguigu.realtime.util.{MyDataUtils, MyKafKaUtils}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Minutes, StreamingContext}
import org.apache.spark.{SparkConf, streaming}

import scala.util.control.Breaks._

/**
  * 需求：同一设备，5分钟内三次及以上用不同账号登录并领取优惠劵，
  * 并且在登录到领劵过程中没有浏览商品。
  * 同时达到以上要求则产生一条预警日志。 同一设备，每分钟只记录一次预警。
  */
object AlertApp {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setAppName("AlertApp").setMaster("local[2]")
    val ssc: StreamingContext = new StreamingContext(conf, streaming.Seconds(5))

    val sourceDStream: InputDStream[(String, String)] = MyKafKaUtils.getKafkaConnection(ssc, ConstantUtil.EVENT_TOPIC)
    //EventLog(mid_436,7653,gmall0508,shanghai,ios,event,addFavor,29,37,12,1570620002595,null,null)
    val eventLogDStream: DStream[(String, EventLog)] = sourceDStream.window(Minutes(5))
      .map {
        case (_, jsonValue) =>
          val eventLog: EventLog = JSON.parseObject(jsonValue, classOf[EventLog])
          eventLog.logDate = MyDataUtils.getYearMonDay(eventLog.ts)
          eventLog.logHour = MyDataUtils.getHour(eventLog.ts)
          (eventLog.mid, eventLog)
      }
    //判断是否达到预警要求
    val isAlertInfoDStream: DStream[(Boolean, AlertInfo)] = eventLogDStream.groupByKey()
      .map {
        //三次及以上用不同账号登录并领取优惠劵，
        //并且在登录到领劵过程中没有浏览商品
        case (mid, logIt) =>
          val uidSet: util.HashSet[String] = new util.HashSet[String]() //领取了优惠券的uid
        val itemSet: util.HashSet[String] = new util.HashSet[String]() //优惠券对应的商品id
        val eventList: util.HashSet[String] = new util.HashSet[String]() //时间
        var isBrowse: Boolean = false
          breakable {
            logIt.foreach(eventLog => {
              eventList.add(eventLog.eventId)
              if (eventLog.eventId == "coupon") {
                uidSet.add(eventLog.uid)
                itemSet.add(eventLog.itemId)
              } else if (eventLog.eventId == "clickItem") {
                isBrowse = true
                break()
              }
            })
          }
          //组成元组(是否预警,预警信息)
          (!isBrowse && uidSet.size() >= 3, AlertInfo(mid, uidSet, itemSet, eventList, System.currentTimeMillis()))
      }
    //过滤出预警数据,并修改数据结构,利用mid+ts的分钟作为ES表中的id,进行实现每分钟的一次的预警
    val alertInfoUDStream: DStream[(String, AlertInfo)] = isAlertInfoDStream.filter(_._1).map {
      case (_, info) => (info.mid + "_" + MyDataUtils.getMinuteDayAndMonAndYear(info.ts), info)
    }
    alertInfoUDStream.print()
    //保存预警数据到ES
    alertInfoUDStream.foreachRDD(rdd => {
      rdd.foreachPartition(it => {
        ElasticSearchUtils.insertBulk(ConstantUtil.INDEX_NAME_ALERT, it.toIterable)
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
