package com.atguigu.realtime.util

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils

object MyKafKaUtils {

  def getKafkaConnection(ssc: StreamingContext, topic: String): InputDStream[(String, String)] = {
    val params: Map[String, String] = Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> PropertiesUtils.getProperty("config.properties", "kafka.broker.list"),
      ConsumerConfig.GROUP_ID_CONFIG -> PropertiesUtils.getProperty("config.properties", "kafka.group")
    )

    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc,
      params,
      Set(topic)
    )
  }


}
