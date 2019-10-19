package com.atguigu.dw.gmall.mock.util

import com.atguigu.dw.gmall.mock.constant.ConstantUtil
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.core.{Bulk, Index}

object ElasticSearchUtils {

  private val clientFactory: JestClientFactory = new JestClientFactory
  private val config: HttpClientConfig = new HttpClientConfig.Builder(ConstantUtil.SERVER_URL)
    .maxTotalConnection(100)
    .multiThreaded(true)
    .connTimeout(10000)
    .readTimeout(10000)
    .build()
  clientFactory.setHttpClientConfig(config)

  def getClient(): JestClient ={
    clientFactory.getObject
  }

  def insertSingle(indexName:String,source:Any)={
    val client: JestClient = clientFactory.getObject
    val action: Index = new Index.Builder(source)
      .index(indexName)
      .`type`("_doc")
      .build()
    client.execute(action)
    client.shutdownClient()
  }

  def insertBulk(indexName:String,sources:Iterable[Any]):Unit={
    if(sources.isEmpty)return
    val client: JestClient = clientFactory.getObject
    val bulkBuilder: Bulk.Builder = new Bulk.Builder()
      .defaultIndex(indexName)
      .defaultType("_doc")
    sources.foreach{
      case (id:String,data) =>
          bulkBuilder.addAction(new Index.Builder(data).id(id).build())
      case source =>
          bulkBuilder.addAction(new Index.Builder(source).build())
    }
    client.execute(bulkBuilder.build())
    client.shutdownClient()
  }

  def main(args: Array[String]): Unit = {
    val source: String =
      """
        |{"id":1,"name":"zhangsan","sex":"male"}
      """.stripMargin
    val source2 = User(2,"lili","female")
   // insertSingle("user",source2)
    insertBulk("user",Array(User(4,"ll","male"),User(33,"zhanglei","male")))
  }

}

case class User(id:Long,name:String,sex:String)
