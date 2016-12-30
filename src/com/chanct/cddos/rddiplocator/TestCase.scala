package com.chanct.cddos.rddiplocator

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql._
import org.apache.commons.lang.StringUtils

object TestCase {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("test-IPExt")
    conf.set("spark.ui.port", "4099")
    conf.set("spark.app.id", "test-IPExt")
    conf.set("spark.dynamicAllocation.maxExecutors", "10")

    val sc = new SparkContext(conf)
    println(s"#### Application ID = ${sc.applicationId} ####")

    val fileName = "/opt/spark-1.6.2-bin-hadoop2.6/conf/cddos_resource/mydata4vipday2.datx"
    println(s"[fileName]: ${fileName}")

    val locator = new RDDIPLocator(sc, fileName)
    val longIPArray = Array(16843528L,
      16857665L,
      16925176L,
      17002849L,
      17048490L,
      34936457L,
      50331648L,
      50478017L,
      51854295L,
      52575201L)

    val longIPRDD = sc.parallelize(longIPArray, 2)
    val IPLocationRDD = locator.locateRDD(longIPRDD).map { elem => (elem._1, elem._2.nation) }.persist()
    println(s"IPLocationRDD.count(): ${IPLocationRDD.count()}")

    IPLocationRDD.collect().foreach { elem => println(s"longIP:${elem._1} --> nation: ${elem._2}") }

    IPLocationRDD.unpersist(true)

    println(s"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    val strIPArray = Array("1.1.3.8",
      "1.1.58.65",
      "1.2.65.248",
      "1.3.113.97",
      "1.4.35.170",
      "2.21.22.137",
      "3.0.0.0",
      "3.2.59.193",
      "3.23.59.215",
      "3.34.59.225")

    val strIPRDD = sc.parallelize(strIPArray, 2)
    val strIPLocationRDD = locator.locateRDD(strIPRDD).map { elem => (elem._1, elem._2.province) }.persist()
    println(s"strIPLocationRDD.count(): ${strIPLocationRDD.count()}")

    strIPLocationRDD.collect().foreach { elem => println(s"strIP: ${elem._1} --> province: ${elem._2}") }

    strIPLocationRDD.unpersist(true)
  }
}