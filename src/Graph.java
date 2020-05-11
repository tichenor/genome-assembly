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

    /**
     * The number of vertices is passed into the constructor in order to allocate the necessary memory
     * for the array capacity. Once an instance is created, the number of vertices cannot be changed.
     * @param numVertices The number of vertices.
     */
   public Graph(int numVertices) {
       graph = new ArrayList<>(numVertices); // Set initial capacity of the array so it doesn't need to dynamically resize.
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
       return sum / 2; // Each edge is counted twice in an undirected graph.
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

    /**
     * Compute the number of connected component as well as their size (number of vertices) using
     * an iterative depth first search.
     * @return A list of integers corresponding to component sizes. The size of the array is the number of components.
     */
   public List<Integer> findConnectedComponents() {
       List<Integer> components = new ArrayList<>();
       boolean[] visited = new boolean[graph.size()]; // Initialize all vertices as unvisited
       int index = 0; // Index of current component being processed
       for (int i = 0; i < graph.size(); i++) { // Go through every vertex
           if (!visited[i]) {
               components.add(0); // Add a new component of size 0
               depthFirstSearch(i, visited, components, index); // Do a depth first search on each unvisited vertex
               index++;
           }
       }
       return components;
   }

   private void depthFirstSearch(int vertex, boolean[] visited, List<Integer> components, int currentComponent) {
       Stack<Integer> stack = new Stack<>();
       stack.push(vertex);
       while (!stack.empty()) {
           int v = stack.pop();
           if (!visited[v]) {
               int newValue = components.get(currentComponent) + 1;
               components.set(currentComponent, newValue); // Increment the component size by one as we visit v.
               visited[v] = true;
           }
           for (int adjacent : graph.get(v)) {
               if (!visited[adjacent]) {
                   stack.push(adjacent); // Push any neighbors onto the stack.
               }
           }
       }
   }
}
