package com.atguigu.realtime.app

import com.alibaba.fastjson.JSON
import com.atguigu.dw.gmall.mock.constant.ConstantUtil
import com.atguigu.realtime.bean.OrderInfo
import com.atguigu.realtime.util.MyKafKaUtils
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.{SparkConf, streaming}

object OrderApp {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("OrderApp").setMaster("local[2]")
    val ssc: StreamingContext = new StreamingContext(conf, streaming.Seconds(3))

    val sourceDStream: InputDStream[(String, String)] = MyKafKaUtils.getKafkaConnection(ssc, ConstantUtil.ORDER_TOPIC)
    val orderInfoDStream: DStream[OrderInfo] = sourceDStream.map {
      case (_, jsonValue) => {
        val orderInf: OrderInfo = JSON.parseObject(jsonValue, classOf[OrderInfo])
        val splits: Array[String] = orderInf.create_time.split(" ")
        orderInf.create_date = splits(0)
        orderInf.create_hour = splits(1).split(":")(0)
        orderInf.consignee = orderInf.consignee.substring(0, 1) + "***"
        orderInf.consignee_tel = orderInf.consignee_tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")
        orderInf
      }
    }
    import org.apache.phoenix.spark._
    orderInfoDStream.foreachRDD(rdd =>{
      rdd.saveToPhoenix(ConstantUtil.ORDER_TABLE_NAME,
        Seq("ID", "PROVINCE_ID", "CONSIGNEE", "ORDER_COMMENT", "CONSIGNEE_TEL", "ORDER_STATUS", "PAYMENT_WAY", "USER_ID", "IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME", "DELIVERY_ADDRESS", "CREATE_TIME", "OPERATE_TIME", "TRACKING_NO", "PARENT_ORDER_ID", "OUT_TRADE_NO", "TRADE_BODY", "CREATE_DATE", "CREATE_HOUR"),
        zkUrl = Some("hadoop102,hadoop103,hadoop104:2181"))
    })

    ssc.start()
    ssc.awaitTermination()

  }


}
