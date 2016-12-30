package com.chanct.cddos.iplocator

object ScalaTestCase {
  def main(arrays: Array[String]): Unit = {

    val fileName = this.getClass().getResource("/").getPath() + "mydata4vipday2.datx"
    println(s"[fileName]: ${fileName}")

    val locator = new IPLocator(fileName)
    val IPregion = locator.locate("182.207.255.255")
    println(s"""nation: ${IPregion.nation}\n
      province: ${IPregion.province}\n
      city: ${IPregion.city}\n
      county: ${IPregion.county}\n
      operator: ${IPregion.operator}\n
      longitude: ${IPregion.longitude}\n
      latitude: ${IPregion.latitude}""")
  }
}