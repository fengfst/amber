package org.amber

import org.apache.spark.SparkContext
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.graphx.{Edge, EdgeDirection, Graph, VertexId}
import org.apache.spark.rdd.RDD

object SparkTest {

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext("local[1]", "whitebox_test")
    val edge = sc.textFile("/Users/sundays/dev/git/amber/graphx/src/main/resources/edge.data").map(line => {
      val strings = line.split(",")
      Edge(strings(0).toInt, strings(1).toInt, strings(3).toInt)
    }).collect()

    val vertex: Array[(VertexId, (String, String))] = sc.textFile("/Users/sundays/dev/git/amber/graphx/src/main/resources/vertex.data").map(line => {
      val strings = line.split(",")
      (strings(0).toLong, (strings(1), strings(2)))
    }).collect()
    val edgeRdd: RDD[Edge[Int]] = sc.parallelize(edge)
    val vertexRDD: RDD[(VertexId, (String, String))] = sc.parallelize(vertex)
    val graph: Graph[(String, String), Int] = Graph(vertexRDD, edgeRdd)
    singleSource(sc)
  }

  def singleSource(sc: SparkContext): Unit = {
    val graph: Graph[VertexId, Double] = GraphGenerators.logNormalGraph(sc, 10).mapEdges(e => e.attr.toDouble)
    graph.edges.foreach(println)

    val sourceId = 4
    val initialGraph: Graph[Double, Double] = graph.mapVertices((id, _) => {
      if (id == sourceId) 0 else Double.PositiveInfinity
    })

    val sssp = initialGraph.pregel(Double.PositiveInfinity, 10, EdgeDirection.Out)((id, dist, newDist) =>
      math.min(dist, newDist),
      triplet => {
        if (triplet.srcAttr + triplet.attr < triplet.dstAttr) {
          Iterator((triplet.dstId, triplet.srcAttr + triplet.attr))
        } else {
          Iterator.empty
        }
      }, (a, b) => math.min(a, b)
    )
    println(sssp.vertices.collect().mkString("\n"))
  }

}
