import java.lang.reflect.Array;
import java.util.*;

/**
 * Adjacency list implementation of an undirected graph. Uses array index numbers as
 * vertex representations. Each array cell---vertex---contains a list of indices representing
 * the neighbouring vertices of that vertex.
 */
public class Graph {

   List<List<Integer>> graph = new ArrayList<>();

   public Graph() {}

   public Graph(int numVertices) {
       graph = new ArrayList<>(numVertices);
       for (int i = 0; i < numVertices; i++) {
           graph.add(new ArrayList<>());
       }
   }

//   public void newVertex(int vertex) {
//       graph.put(vertex, new ArrayList<>());
//   }

   public void addEdge(int v1, int v2) {
       graph.get(v1).add(v2);
       graph.get(v2).add(v1);
   }

   public int numberOfVertices() {
       return graph.size();
   }

   public int numberOfEdges() {
       int sum = 0;
       for (List<Integer> adjSet : graph) {
           sum += adjSet.size();
       }
       return sum / 2;
   }

}
