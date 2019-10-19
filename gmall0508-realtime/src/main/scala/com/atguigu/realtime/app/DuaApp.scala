package com.atguigu.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.atguigu.dw.gmall.mock.constant.ConstantUtil
import com.atguigu.realtime.bean.StartupLog
import com.atguigu.realtime.util.{MyKafKaUtils, RedisUtils}
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

object DuaApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("DuaApp").setMaster("local[2]")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))
    //1.读取kafka数据
    val topic: String = ConstantUtil.START_TOPIC
    val sourceDStream: InputDStream[(String, String)] = MyKafKaUtils.getKafkaConnection(ssc, topic)
    //2.转换json数据为样例类StartupLog
    val sourceStartupLogDStream: DStream[StartupLog] = sourceDStream.map {
      case (_, value) =>
        val startupLog: StartupLog = JSON.parseObject(value, classOf[StartupLog])
        startupLog.logDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        startupLog.logHour = new SimpleDateFormat("hh").format(new Date())
        startupLog
    }
    //3.1 过滤已经存入redis中的uid
    val filterdDStreamByRedis: DStream[StartupLog] = sourceStartupLogDStream.transform(rdd => {
      val client: Jedis = RedisUtils.getJedisClient
      //可以广播uidSet
      val uidSet: util.Set[String] = client.smembers(topic + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
      val uidSetBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(uidSet)
      client.close()
      rdd.filter(log => {
        val uidSetEx: util.Set[String] = uidSetBC.value
        !uidSetEx.contains(log.uid)
      })
    })

    //3.2 将第一次登陆,且首个批次的的数据进行去重----分组排序取第一个
    val resultFilterDStream: DStream[StartupLog] = filterdDStreamByRedis.map(log => (log.uid, log))
      .groupByKey()
      .map {
        case (_, it) =>
          //  it.toList.sortBy(_.logDate).take(1)(0)
//          it.toList.sortBy(_.logDate).head
          it.toList.minBy(_.logDate)
      }
    //3.3 存入redis ,利用set可以uid去重
    resultFilterDStream.foreachRDD(rdd => {
      rdd.foreachPartition(logIt => {
        val client: Jedis = RedisUtils.getJedisClient
        logIt.foreach(log => {
          client.sadd(topic + ":" + log.logDate, log.uid)
        })
        client.close()
      })
    })
    //4.利用phoenix将数据存入hbase
    import org.apache.phoenix.spark._
    resultFilterDStream.foreachRDD(rdd => {
      rdd.saveToPhoenix("GMALL_DAU",
        Seq("MID", "UID", "APPID", "AREA", "OS", "CH", "TYPE", "VS", "TS", "LOGDATE", "LOGHOUR"),
        zkUrl = Some("hadoop102,hadoop103,hadoop104:2181"))
    })
    ssc.start()
    ssc.awaitTermination()
  }
}
