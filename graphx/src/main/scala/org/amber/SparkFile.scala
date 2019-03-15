package org.amber

import org.apache.spark.SparkContext

object SparkFile {

  def main(args: Array[String]): Unit = {
    val context = new SparkContext("local", "sparkFile")
    val file = context.textFile("/Users/sundays/dev/git/amber/graphx/src/main/resources/edge.data")
    file.collect().foreach(println)
  }
}
