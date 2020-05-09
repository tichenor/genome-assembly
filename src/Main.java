import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String FILEPATH = "res/Spruce_fingerprint_2017-03-10_16.48.olp.m4";

    public static void main(String[] args) {

        Map<String, Integer> indices = CustomReader.indexAllIdentifiers();
        System.out.println("Size: "+ indices.size());

        Graph g = CustomReader.generateFullGraph(indices);
        System.out.println("Graph generated.");
        System.out.println("Vertices: " + g.numberOfVertices());
        System.out.println("Edges: " + g.numberOfEdges());

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
