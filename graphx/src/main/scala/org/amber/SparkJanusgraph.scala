package org.amber

import org.apache.spark.SparkConf
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory

object SparkJanusgraph {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.set("master", "local")
    // 进行节点计算
    val graph = GraphFactory.open("/Users/sundays/dev/git-bak/graphx/src/main/resources/config.properties")
    val g = graph.traversal.withComputer(classOf[SparkGraphComputer])

  }
}
