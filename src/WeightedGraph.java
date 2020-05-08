import java.lang.reflect.Array;
import java.util.*;

public class WeightedGraph {

    private static class Vertex {

        int length;
        int start;
        int end;
        HashMap<Vertex, Float> adjacencies;

        Vertex(int length, int start, int end) {
            this.length = length;
            this.start = start;
            this.end = end;
            adjacencies = new HashMap<>();
        }

        private int degree() {
            return adjacencies.size();
        }

    }

    private HashMap<String, Vertex> graph;

    public WeightedGraph() {
        graph = new HashMap<String, Vertex>();
    }

    public void addVertex(String id, int length, int start, int end) {
        Vertex v = new Vertex(length, start, end);
        graph.put(id, v);
    }

    public void addEdge(String sourceId, String targetId, float weight, boolean reversed) {
        Vertex source = graph.get(sourceId);
        Vertex target = graph.get(targetId);
        if (!reversed) {
            source.adjacencies.put(target, weight);
        } else {
            target.adjacencies.put(source, weight);
        }


    }

    public boolean contains(String id) {
        return graph.containsKey(id);
    }

    public int degreeOf(String id) {
        return graph.get(id).degree();
    }

    public int vertexCount() {
        return graph.size();
    }

    public List<String> getVertices() {
        return new ArrayList<>(graph.keySet());
    }

    public boolean isConnected() {
        int n = graph.size();
        boolean[] visited1 = new boolean[n];
        boolean[] visited2 = new boolean[n];
        Arrays.fill(visited1, false);
        return true;
    }

}
