import java.util.*;

public class Main {

    public static final String FILEPATH = "res/Spruce_fingerprint_2017-03-10_16.48.olp.m4";

    public static void main(String[] args) {

        /*
         * Run a full computation on the data set. The data is assumed to have been split into chunks and filtered
         * with the FilteredCopyTask in LineParserParallel.
         */

        // Index all unique string identifiers to integers.
        System.out.println("Indexing identifiers...");
        long start = System.nanoTime();
        Map<String, Integer> indices = LineParser.indexAllIdentifiers();
        long end = System.nanoTime();
        System.out.println();
        CustomWriter.testDuration(start, end);
        System.out.println("Unique identifiers found: "+ indices.size());
        System.out.println("--------------------");

        // Generate a graph from the integer indices.
        System.out.println("Generating graph from indices...");
        start = System.nanoTime();
        Graph graph = LineParser.generateFullGraph(indices);
        end = System.nanoTime();
        System.out.println();
        System.out.println("Graph generated.");
        CustomWriter.testDuration(start, end);
        System.out.println("Vertices: " + graph.numberOfVertices());
        System.out.println("Edges: " + graph.numberOfEdges());
        System.out.println("--------------------");

        // The following block computes and writes the degree frequencies to a text file.
        System.out.println("Finding degree distribution...");
        start = System.nanoTime();
        Map<Integer, Integer> degFreqs = graph.getDegreeDistribution();
        end = System.nanoTime();
        CustomWriter.testDuration(start, end);
        System.out.println("Found " + degFreqs.size() + " different degree values of graph.");
        CustomWriter.writeMapToFile(degFreqs, "degreeFrequencies",
                "Degree frequencies -- (degree):(vertex count) -- " + degFreqs.size() + " entries.");
        System.out.println("--------------------");

        // The following block computes writes the components and their sizes to a text file.
        System.out.println("Finding connected components...");
        start = System.nanoTime();
        List<Integer> components = graph.findConnectedComponents();
        end = System.nanoTime();
        CustomWriter.testDuration(start, end);
        System.out.println("Found " + components.size() + " connected components.");
        CustomWriter.writeListToFile(components, "components",
                "Connected components -- (component number):(number of vertices) -- " + components.size() +
                        " components.");
        System.out.println("--------------------");
        System.out.println("Program finished. Garbage collection might take a few seconds.");

        // Counting the lines (data points) of the whole file.
//        final String filename = "res/Spruce_fingerprint_2017-03-10_16.48.olp.m4";
//        int lineCount = -1;
//        try {
//            lineCount = CustomReader.lineCount(filename);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(lineCount);

//        // Counting the data points of the filtered copies after removing false overlaps.
//        final String fileDirPrefix = "res/splits/chunkF";
//        int totalCount = 0;
//        long start = System.nanoTime();
//        try {
//            for (int i = 0; i < 641; i++) {
//                totalCount += CustomReader.lineCount(fileDirPrefix + String.format("%04d", i));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        long end = System.nanoTime();
//        long dur = TimeUnit.NANOSECONDS.toMillis((end - start));
//        System.out.println("Line count took " + dur + " ms.");
//        System.out.println("Total true overlaps in data set: " + totalCount);
    }

}
