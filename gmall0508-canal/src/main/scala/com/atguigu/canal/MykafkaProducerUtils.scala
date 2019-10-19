package com.atguigu.canal

import java.util.Properties
import java.util.concurrent.Future

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

object MykafkaProducerUtils {

  val props: Properties = new Properties()
  // Kafka服务端的主机名和端口号
  props.put("bootstrap.servers", "hadoop103:9092,hadoop102:9092,hadoop104:9092")
  // key序列化
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  // value序列化
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer: KafkaProducer[String, String] = new KafkaProducer[String,String](props)

  def send(topic:String,content:String): Future[RecordMetadata] ={
    producer.send(new ProducerRecord[String,String](topic,content))
  }

}
