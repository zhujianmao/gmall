package com.atguigu.realtime.util

import java.io.InputStream
import java.util.Properties

import scala.collection.mutable

object PropertiesUtils {

  val map = mutable.Map[String,Properties]()


  def getProperties(fileName:String): Properties ={
    val properties: Properties = new Properties()
    val classLoader: InputStream = ClassLoader.getSystemResourceAsStream(fileName)
    properties.load(classLoader)
    map.put(fileName,properties)
    properties
  }

  def getProperty(fileName:String,propertyName:String): String ={
    val props: Properties = map.getOrElseUpdate(fileName, {
      val properties: Properties = new Properties()
      val classLoader: InputStream = ClassLoader.getSystemResourceAsStream(fileName)
      properties.load(classLoader)
      map.put(fileName, properties)
      properties
    })
    props.getProperty(propertyName,"null")
  }

}
