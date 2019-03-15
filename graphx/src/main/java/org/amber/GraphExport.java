package org.amber;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

/**
 * @Author: haifeng
 * @Date: 2019-03-14 14:33
 */
public class GraphExport {
    public static void main(String[] args) {
        Graph graph = EmptyGraph.instance();

        GraphTraversalSource g = graph.traversal().withRemote(DriverRemoteConnection.using("192.168.21.21", 8182, "g"));
        System.out.println(g.V().count().next());

        GraphTraversal<Vertex, Vertex> v = g.V();
        while (v.hasNext()) {
            Vertex next = v.next();
            VertexProperty<Object> property = next.property("vertexName");
            System.out.println(property.key() + "---->" + property.value().toString());
        }

       

    }
}
