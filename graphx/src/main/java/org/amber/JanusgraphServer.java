package org.amber;

import org.apache.spark.storage.StorageLevel;
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;


public class JanusgraphServer {
    public static void main(String[] args) {
        // 进行节点计算
        Graph graph = GraphFactory.open("/Users/sundays/dev/git/amber/graphx/src/main/resources/config.properties");
        GraphTraversalSource g = graph.traversal().withComputer(SparkGraphComputer.class);
        g.io("graph.graphml").with("GraphComputer", "SparkGraphComputer").with(IO.writer, IO.graphml).write().iterate();

    }
}