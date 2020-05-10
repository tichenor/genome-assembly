import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Adjacency list implementation of an undirected graph. Uses array index numbers as
 * vertex representations. Each array cell---vertex---contains a list of indices representing
 * the neighbouring vertices of that vertex.
 */
public class Graph implements Serializable {

   List<List<Integer>> graph;

   public Graph(int numVertices) {
       graph = new ArrayList<>(numVertices);
       for (int i = 0; i < numVertices; i++) {
           graph.add(new ArrayList<>());
       }
   }

   public void addEdge(int v1, int v2) {
       graph.get(v1).add(v2);
       graph.get(v2).add(v1);
   }

   public int numberOfVertices() {
       return graph.size();
   }

   public int numberOfEdges() {
       int sum = 0;
       for (List<Integer> adjList : graph) {
           sum += adjList.size();
       }
       return sum / 2;
   }

   public int[] getAllDegrees() {
       int[] degrees = new int[graph.size()];
       for (int i = 0; i < graph.size(); i++) {
           degrees[i] = graph.get(i).size();
       }
       return degrees;
   }

    /**
     * Find the degrees of all vertices and count the number of vertices there
     * are of a given degree.
     * @return A map where keys are degrees and values are total number of vertices of that degree.
     */
   public Map<Integer, Integer> getDegreeDistribution() {
       Map<Integer, Integer> degFrequencies = new HashMap<>();
       for (List<Integer> adjList : graph) {
           int deg = adjList.size(); //TODO: should check for duplicates in adjacency list?
           Integer count = degFrequencies.get(deg);
           if (count == null) {
               degFrequencies.put(deg, 1);
           } else {
               degFrequencies.put(deg, ++count);
           }
       }
       return degFrequencies;
   }

}
