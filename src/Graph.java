import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Adjacency list implementation of an undirected graph. Uses array index numbers as
 * vertex representations. Each array cell---vertex---contains a list of indices (adjacency list of integers)
 * representing the neighbouring vertices of that vertex.
 */
public class Graph {

   private final List<List<Integer>> graph; // Internal graph representation.

    /**
     * The number of vertices is passed into the constructor in order to allocate the necessary memory
     * for the array capacity. Once an instance is created, its number of vertices cannot be changed.
     * @param numVertices The number of vertices.
     */
   public Graph(int numVertices) {
       graph = new ArrayList<>(numVertices); // Set initial capacity of the array so it doesn't need to dynamically resize.
       for (int i = 0; i < numVertices; i++) {
           graph.add(new ArrayList<>());
       }
   }

    /**
     * Adding an edge between to vertices in an undirected graph corresponds to appending the integers representing
     * the vertices to their corresponding adjacency lists.
     * @param v1 An integer representing the first vertex.
     * @param v2 An integer representing the second vertex.
     */
   public void addEdge(int v1, int v2) {
       graph.get(v1).add(v2);
       graph.get(v2).add(v1);
   }

   public int numberOfVertices() {
       return graph.size();
   }

    /**
     * Count the number of edges in the graph. The number of edges in an undirected graph is the sum of the degrees of
     * its vertices divided by 2.
     * @return An integer representing the total number of edges in the graph.
     */
   public int numberOfEdges() {
       int sum = 0;
       for (List<Integer> adjList : graph) {
           sum += adjList.size();
       }
       return sum / 2; // Each edge is counted twice in the degree sum.
   }

    /**
     * Find the degrees of all vertices and count the number of vertices there
     * are of a given degree.
     * @return A map where keys are degrees and values are total number of vertices of that degree.
     */
   public Map<Integer, Integer> getDegreeDistribution() {
       Map<Integer, Integer> degFrequencies = new HashMap<>();
       for (List<Integer> adjList : graph) { // Go through every vertex of the graph
           int deg = adjList.size(); // Get its degree
           Integer count = degFrequencies.get(deg); // Check if there are any other vertices of that degree
           if (count == null) {
               degFrequencies.put(deg, 1); // If not, we put 1 as the value
           } else {
               degFrequencies.put(deg, ++count); // Otherwise we increment the vertex count by 1
           }
       }
       return degFrequencies;
   }

    /**
     * Compute the number of connected components as well as their size (number of vertices) using
     * an iterative depth first search and return a list of the component sizes. The size of the list is the
     * number of components.
     * @return A list of integers representing component sizes.
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

    /**
     * Iterative depth first search using a stack.
     * @param vertex The starting vertex.
     * @param visited An array of booleans representing which vertices have been visited (processed).
     * @param components The component size list.
     * @param currentComponent The index of the current component.
     */
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
