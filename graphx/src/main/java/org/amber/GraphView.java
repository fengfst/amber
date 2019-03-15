package org.amber;

import com.alibaba.fastjson.JSON;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

import java.util.Map;

/**
 * @Author: haifeng
 * @Date: 2019-03-14 16:09
 */
public class GraphView {
    public static void main(String[] args) throws Exception {
        Graph graph = GraphFactory.open("/Users/sundays/dev/git/amber/graphx/src/main/resources/janusgraph.properties");
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Map<Object, Object>> name = g.V().has("vertexName", "PM2.5:徐州市").valueMap();
        System.out.println(JSON.toJSONString(name));

        GraphTraversal<Vertex, Path> limit = g.V().has("vertexName", "空气质量").repeat(__.outE().has("visibility", true).has("isConductionPath").inV().has("visility", true)).until(__.has("vertexName", "钢铁供给")).path().limit(1);

        graph.close();
    }
}
