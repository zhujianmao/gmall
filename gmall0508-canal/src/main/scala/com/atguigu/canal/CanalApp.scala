package com.atguigu.canal

import java.net.InetSocketAddress
import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, EventType, RowChange, RowData}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.google.protobuf.ByteString

/**
  * 1. 建立连接
  *
  * 2. 通过连接读数据
  *
  * Message get一次获取一个Message, 一个Message表示一批数据, 看出多条sql语句执行的结果
  * Entry  一个Message封装多个Entry, 一个Entry可以看出一条sql语句执行的多行结果
  * StoreValue 一个 Entry封装一个 storeValue, 可以看到是是数据序列化形式
  * RowChange 从StoreValue里面解析出来的数据类型
  * RowData 一个rowChage会有多个RowData, 一个RowData封装了一行数据
  * Column 列
  */
object CanalApp {
  def main(args: Array[String]): Unit = {

    val canalConnector: CanalConnector = CanalConnectors.newSingleConnector(
      new InetSocketAddress("hadoop102", 11111),
      "example",
      "",
      "")
    canalConnector.connect()
    canalConnector.subscribe("gmall.*")
    while (true) {
      //1. Message get一次获取一个Message, 一个Message表示一批数据, 看出多条sql语句执行的结果
      val message: Message = canalConnector.get(500)
      //2. Entry  一个Message封装多个Entry, 一个Entry可以看出一条sql语句执行的多行结果
      val entries: util.List[CanalEntry.Entry] = message.getEntries
      import scala.collection.JavaConversions._
      if (entries != null && !entries.isEmpty) {
        for (entry <- entries) {
          //EntryType为ROWDATA时才处理
          if (entry.getEntryType == EntryType.ROWDATA) {
            //3. StoreValue 一个 Entry封装一个 storeValue, 可以看到是是数据序列化形式
            val storeValue: ByteString = entry.getStoreValue
            //4. RowChange 从StoreValue里面解析出来的数据类型
            val rowChange: RowChange = RowChange.parseFrom(storeValue)
            //5. 只处理insert类型的eventType 且 order_info表
            if (rowChange.getEventType == EventType.INSERT && "order_info" == entry.getHeader.getTableName) {
              //6. RowData 一个rowChange会有多个RowData, 一个RowData封装了一行数据
              val rowDatas: util.List[RowData] = rowChange.getRowDatasList
              if (!rowDatas.isEmpty) {
                //Column 列
                for (rowData <- rowDatas) {
                  val jsonObj: JSONObject = new JSONObject()
                  val columnsList: util.List[CanalEntry.Column] = rowData.getAfterColumnsList
                  for (column <- columnsList) {
                    jsonObj.put(column.getName, column.getValue)
                  }
                  println(jsonObj.toString)
                }
              }
            }

          }
        }

      } else {
        println("暂时没有数据发生新增,过2秒再查询")
        Thread.sleep(2000)
      }
    }


  }
}
