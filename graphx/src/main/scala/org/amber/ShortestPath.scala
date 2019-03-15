package org.amber

import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD

object ShortestPath {

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext("local", "ShortestPath")
    val edge = sc.textFile("/Users/sundays/dev/git/amber/graphx/src/main/resources/edge.data").map(line => {
      val strings = line.split(",")
      Edge(strings(0).toInt, strings(1).toInt, strings(2).toInt)
    }).collect()
    val vertex: Array[(VertexId, String)] = sc.textFile("/Users/sundays/dev/git/amber/graphx/src/main/resources/vertex.data").map(line => {
      val strings = line.split(",")
      (strings(0).toLong, strings(1))
    }).collect()
    val edgeRdd: RDD[Edge[Int]] = sc.parallelize(edge)
    val vertexRdd: RDD[(VertexId, String)] = sc.parallelize(vertex)
    val graph = Graph(vertexRdd, edgeRdd)
    val sourceId: Int = 1
    val subGraph = graph.mapVertices((id, _) => {
      if (id == sourceId) (0.0, sourceId + "") else (Double.PositiveInfinity, "")
    })
    val sssp = subGraph.pregel((Double.PositiveInfinity, ""))((id, dist, newDist) => {
      if (dist._1 < newDist._1) dist else newDist
    }, triplet => {
      if (triplet.srcAttr._1 + triplet.attr < triplet.dstAttr._1) {
        Iterator((triplet.dstId, (triplet.srcAttr._1 + triplet.attr, triplet.srcAttr._2 + "->" + triplet.dstId)))
      } else {
        Iterator.empty
      }
    }, (a, b) => {
      if (a._1 > b._1) {
        a
      } else {
        b
      }
    })
    println(sssp.vertices.collect().mkString("\n"))

  }
}
