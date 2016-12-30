package com.chanct.cddos.iplocator

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.HeapByteBuffer
import java.nio.charset.Charset

import scala.math._
import scala.util.control.Breaks._

import org.apache.commons.lang.StringUtils

case class IPLocation(
  nation: String = "",
  province: String = "",
  city: String = "",
  county: String = "",
  operator: String = "",
  longitude: String = "",
  latitude: String = "")

class IPLocator(fileName: String) {
  class material {
    var dataByteArray: Array[Byte] = Array[Byte]()
    var indexByteArray: Array[Byte] = Array[Byte]()
    var indexIntArray: Array[Int] = Array[Int]()
    var offset: Int = 0
  }

  val datxFileName = fileName
  private val IPInfoArray: material = getIPInfoArray

  def locate(longIP: Long): IPLocation = {
    val ipRegion: Array[String] = new Array[String](7)

    val dataByteArray: Array[Byte] = IPInfoArray.dataByteArray
    val indexByteArray: Array[Byte] = IPInfoArray.indexByteArray
    val indexIntArray: Array[Int] = IPInfoArray.indexIntArray
    val offset: Int = IPInfoArray.offset

    val dataByteBuffer = ByteBuffer.wrap(dataByteArray)
    val indexByteBuffer = ByteBuffer.wrap(indexByteArray)

    val ips: Array[String] = IPlong2string(longIP).split("\\.")
    println(s"*--- ip :${ips.toBuffer.toString()} ---*")
    val prefix_value: Int = ips(0).toInt * 256 + ips(1).toInt
    var start: Int = indexIntArray(prefix_value)
    val max_comp_len: Int = offset - 262144 - 4

    var tmpInt: Long = 0L
    var index_offset: Long = -1
    var index_length: Long = -1
    var b: Byte = 0
    start = start * 9 + 262144

    breakable {
      while (start < max_comp_len) {
        tmpInt = int2long(indexByteBuffer.getInt(start))
        if (tmpInt >= longIP) {
          index_offset = bytesToLong(b, indexByteBuffer.get(start + 6), indexByteBuffer.get(start + 5), indexByteBuffer.get(start + 4))
          index_length = (0xFF & indexByteBuffer.get(start + 7) << 8) + (0xFF & indexByteBuffer.get(start + 8))
          break
        }
        start += 9
      }
      // get tmpInt,index_offset, index_length from ip2long_value
    }

    dataByteBuffer.position(offset + index_offset.toInt - 262144)
    val areaBytes: Array[Byte] = new Array[Byte](index_length.toInt)
    dataByteBuffer.get(areaBytes, 0, index_length.toInt)

    val arrs2: Array[String] = new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1)

    ipRegion(0) = arrs2(0)
    ipRegion(1) = arrs2(1)
    ipRegion(2) = arrs2(2)
    ipRegion(3) = arrs2(2)
    ipRegion(4) = arrs2(4)
    ipRegion(6) = arrs2(5)

    if (StringUtils.isBlank(ipRegion(3))) {
      ipRegion(3) = ipRegion(1)
    }

    val region: IPLocation = IPLocation(ipRegion(0), ipRegion(1), ipRegion(2), ipRegion(3), ipRegion(4), ipRegion(5), ipRegion(6))
    region
  }

  def locate(strIP: String): IPLocation = {
    val ipRegion: Array[String] = new Array[String](7)

    val longIP = StringIP2Long(strIP)

    val dataByteArray: Array[Byte] = IPInfoArray.dataByteArray
    val indexByteArray: Array[Byte] = IPInfoArray.indexByteArray
    val indexIntArray: Array[Int] = IPInfoArray.indexIntArray
    val offset: Int = IPInfoArray.offset

    val dataByteBuffer = ByteBuffer.wrap(dataByteArray)
    val indexByteBuffer = ByteBuffer.wrap(indexByteArray)

    val ips: Array[String] = strIP.split("\\.")
    println(s"*--- ip :${ips.toBuffer.toString()} ---*")
    val prefix_value: Int = ips(0).toInt * 256 + ips(1).toInt
    var start: Int = indexIntArray(prefix_value)
    val max_comp_len: Int = offset - 262144 - 4

    var tmpInt: Long = 0L
    var index_offset: Long = -1
    var index_length: Long = -1
    var b: Byte = 0
    start = start * 9 + 262144

    breakable {
      while (start < max_comp_len) {
        tmpInt = int2long(indexByteBuffer.getInt(start))
        if (tmpInt >= longIP) {
          index_offset = bytesToLong(b, indexByteBuffer.get(start + 6), indexByteBuffer.get(start + 5), indexByteBuffer.get(start + 4))
          index_length = (0xFF & indexByteBuffer.get(start + 7) << 8) + (0xFF & indexByteBuffer.get(start + 8))
          break
        }
        start += 9
      }
      // get tmpInt,index_offset, index_length from ip2long_value	  
    }

    dataByteBuffer.position(offset + index_offset.toInt - 262144)
    val areaBytes: Array[Byte] = new Array[Byte](index_length.toInt)
    dataByteBuffer.get(areaBytes, 0, index_length.toInt)

    val arrs2: Array[String] = new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1)

    ipRegion(0) = arrs2(0)
    ipRegion(1) = arrs2(1)
    ipRegion(2) = arrs2(2)
    ipRegion(3) = arrs2(2)
    ipRegion(4) = arrs2(4)
    ipRegion(6) = arrs2(5)

    if (StringUtils.isBlank(ipRegion(3))) {
      ipRegion(3) = ipRegion(1)
    }

    val region: IPLocation = IPLocation(ipRegion(0), ipRegion(1), ipRegion(2), ipRegion(3), ipRegion(4), ipRegion(5), ipRegion(6))
    region
  }

  private def getIPInfoArray(): material = {

    val arrays: material = new material()

    val ipFile: File = new File(datxFileName)
    var fin: FileInputStream = null
    val length = ipFile.length().toInt
    var dataByteArray: Array[Byte] = new Array[Byte](length)
    try {
      fin = new FileInputStream(ipFile)
      var readBytesLength: Int = 0
      var i: Int = fin.available()
      while (i > 0) {
        fin.read(dataByteArray, readBytesLength, i)
        readBytesLength += i
        i = fin.available()
      }
    } catch {
      case e: Throwable => {
        e.printStackTrace
      }
    } finally {
      try {
        if (fin != null) {
          fin.close()
        }
      } catch {
        case e: Throwable => {
          e.printStackTrace
        }
      }
    }

    println(s"dataByteArray.length: ${dataByteArray.length}")
    val dataBuffer: ByteBuffer = ByteBuffer.wrap(dataByteArray)
    println(s"dataBuffer.capacity(): ${dataBuffer.capacity()}")
    val offset: Int = dataBuffer.getInt()
    println(s"offset: ${offset}")
    val indexByteArray: Array[Byte] = new Array[Byte](offset)
    dataBuffer.get(indexByteArray, 0, offset - 4)

    val indexIntArray: Array[Int] = new Array[Int](65536)
    val indexByteBuffer: ByteBuffer = ByteBuffer.wrap(indexByteArray)
    indexByteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    for (i <- 0.until(256)) {
      for (j <- 0.until(256)) {
        indexIntArray(i * 256 + j) = indexByteBuffer.getInt()
      }
    }
    indexByteBuffer.order(ByteOrder.BIG_ENDIAN)

    arrays.dataByteArray = dataByteArray
    arrays.indexByteArray = indexByteArray
    arrays.indexIntArray = indexIntArray
    arrays.offset = offset

    arrays
  }

  private def StringIP2Long(ip: String): Long = {
    println("this is StringIP2Long: " + ip)
    var ip_src = ip;
    var ip_result = 0L;

    var string_array = ip_src.split("\\.")
    println("length of string_array is: " + string_array.length)
    for (i <- 0 until string_array.length) {
      var temp = Integer.parseInt(string_array(i))
      var _temp = pow(256, (3 - i)).toLong
      ip_result = ip_result + temp * _temp
    }

    ip_result
  }

  private def IPlong2string(ip: Long): String = {
    var ips = new Array[Long](4)
    var t = ip
    for (i <- List(3, 2, 1, 0)) { ips(i) = t % 256; t = t / 256 }
    val ipString = ips.mkString(".")
    ipString
  }

  private def int2long(i: Int): Long = {
    var l: Long = i & 0x7fffffffL
    if (i < 0) {
      l |= 0x080000000L
    }
    l
  }

  private def bytesToLong(a: Byte, b: Byte, c: Byte, d: Byte): Long = {
    int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)))
  }
}