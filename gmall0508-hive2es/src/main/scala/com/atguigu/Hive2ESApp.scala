package com.atguigu

import java.io
import java.sql.Timestamp
import java.util.Date

import com.atguigu.dw.gmall.mock.constant.ConstantUtil
import com.atguigu.dw.gmall.mock.util.ElasticSearchUtils
import org.apache.spark.sql.{DataFrame, SparkSession}

object Hive2ESApp {
  def main(args: Array[String]): Unit = {
    val date = "2019-10-11"
    val spark: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("Hive2ESApp")
      .enableHiveSupport()
      .getOrCreate()
    val sql =
      s"""
select
    user_id,
    sku_id,
    user_gender,
    cast(user_age as int) user_age,
    user_level,
    cast(order_price as double) order_price,
    sku_name,
    sku_tm_id,
    sku_category3_id,
    sku_category2_id,
    sku_category1_id,
    sku_category3_name,
    sku_category2_name,
    sku_category1_name,
    spu_id,
    sku_num,
    cast(order_count as bigint) order_count,
    cast(order_amount as double) order_amount,
    dt
from dws_sale_detail_daycount
where dt='$date'
             """.stripMargin


    val df: DataFrame = spark.sql(sql)
    import spark.implicits._
    df.as[SaleDetailDayCount].foreachPartition(it => {
      ElasticSearchUtils.insertBulk(ConstantUtil.INDEX_NAME_SALE,it.toIterable)
    })

    spark.close()
  }
}
