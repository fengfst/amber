package org.amber;

import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

/**
 * @Author: haifeng
 * @Date: 2019-03-14 16:17
 */
public class GraphImport {
    public static void main(String[] args) throws Exception {
        Graph graph = GraphFactory.open("/Users/sundays/dev/git/amber/graphx/src/main/resources/janusgraph-cassandra.properties");
        GraphTraversalSource g = graph.traversal();
        Long count = g.V().count().next();
        System.out.println("vertex count:" + count);
        GraphTraversal<Vertex, Vertex> v = g.V();
        while (v.hasNext()) {
            v.next().remove();
            g.tx().commit();
        }

        System.out.println(g.V().count().next());
        g.io("tmp.graphml").with(IO.reader, IO.graphml).read().iterate();
        System.out.println(g.V().count().next());
        graph.close();
    }
}
