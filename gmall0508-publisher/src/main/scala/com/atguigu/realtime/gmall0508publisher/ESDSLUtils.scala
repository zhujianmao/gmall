package com.atguigu.realtime.gmall0508publisher

object ESDSLUtils {


  def getSaleDSL(date:String, keyword:String, startPage:Long, size:Long, aggSize:Long, field:String): String ={
    s"""
      |{
      |  "from": ${(startPage-1)*size},
      |  "size": ${size},
      |  "query": {
      |    "bool": {
      |      "filter": {
      |        "term": {
      |          "dt": "${date}"
      |        }
      |      },
      |      "must":
      |        {"match": {
      |          "sku_name": {
      |            "query":"${keyword}",
      |            "operator":"and"
      |          }
      |        }}
      |    }
      |  },"aggs": {
      |    "groupby_${field}": {
      |      "terms": {
      |        "field": "${field}",
      |        "size": ${aggSize}
      |      }
      |    }
      |  }
      |}
    """.stripMargin
  }
}
