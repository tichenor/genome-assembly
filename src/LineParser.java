import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class that handles reading from the data file, doing various things with it such as counting
 * the number of lines, indexing string identifiers as integers, and generating graphs.
 */
public class LineParser {

    static final String DELIMITER = "\t";
    static final byte NEWLINE = '\n';

    /**
     * Implementation of a line counting algorithm. Uses a buffered input stream to read a certain number
     * of bytes at a time, looking for the newline escape sequence '\n'. The goal of this implementation was to make it
     * as fast as possible using only Java.
     * @param filename The name or location of the file to count lines in.
     * @return The number of lines found.
     * @throws IOException
     */
    public static int lineCount(String filename) throws IOException {
        // Probably only works for ASCII or UTF-8 character encoded files
        // Note: This can easily be done using the word count Unix command with newline option: wc -l
        // Testing a larger buffer size on the BufferedInputStream resulted in faster runtime (default is 8 kB)
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename), 1024 * 64)) {
            byte[] b = new byte[1024]; // For some reason, 1024 (1 kB) seems very fast
            int readChars = is.read(b); // Read the next 1024 bytes
            if (readChars == -1) { // .read returns -1 if end of file is reached
                is.close();
                return 0;
            }

            int lineCount = 0;
            // Main body of the function; check for line breaks in the byte buffer
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (b[i++] == NEWLINE) {
                        ++lineCount;
                    }
                }
                readChars = is.read(b);
            }

            // Search the remaining bytes (should be less than 1024 at this point)
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (b[i] == NEWLINE) {
                        ++lineCount;
                    }
                }
                readChars = is.read(b);
            }
            is.close();
            return lineCount == 0 ? 1 : lineCount;
        }
    }

    /**
     * This method constructs a map/dictionary between string identifiers and integers from the spruce fingerprint
     * data file. Each unique identifier found in the data file is mapped to a unique integer identifier. This method
     * is a first version of the 'indexAllIdentifiers' method, and was used for testing purposes on a small sample of
     * the original data file.
     * @param filename The name or location of the file to read from.
     * @return A map/dictionary between string identifiers and integers.
     */
    public static Map<String, Integer> indexIdentifiers(String filename) {
        Map<String, Integer> indices = new HashMap<>();
        FileReader fileReader;
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return indices;
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        String[] tokens;
        int indexCount = 0;
        int printVal = 0, testCounter = 0;
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) != null) {
                    tokens = line.split("\t");
                    if (indices.get(tokens[0]) == null) {
                        indices.put(tokens[0], indexCount++);
                    }
                    if (indices.get(tokens[1]) == null) {
                        indices.put(tokens[1], indexCount++);
                    }
                } else break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            testCounter++;
            if (testCounter == 1_000_000) {
                printVal += 1000000;
                testCounter = 0;
                System.out.println("Processed: " + printVal);
            }
        }
        return indices;
    }

    /**
     * Custom method that reads each chunk of the original data file (that was split into smaller parts) and maps
     * each unique string identifier to a unique integer in a map/dictionary. This is the final version of the
     * 'indexIdentifiers' method and is used to process the whole data set.
     * @return A map/dictionary between string identifiers and integers.
     */
    public static Map<String, Integer> indexAllIdentifiers() {
        Map<String, Integer> indices = new HashMap<>();
        FileReader fileReader;
        String line;
        String[] tokens;
        int indexCount = 0;
        for (int i = 0; i < 641; i++) { // There are 641 chunks/parts.
            try {
                fileReader = new FileReader("res/splits/chunkF" + String.format("%04d", i));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while (true) {
                    if ((line = bufferedReader.readLine()) != null) {
                        tokens = line.split(DELIMITER);
                        if (indices.get(tokens[0]) == null) {
                            indices.put(tokens[0], indexCount++);
                        }
                        if (indices.get(tokens[1]) == null) {
                            indices.put(tokens[1], indexCount++);
                        }
                    } else break;
                }
                fileReader.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            double percentage = ((double) i) / 640;
            CustomWriter.updateProgress(percentage);
        }
        return indices;
    }

    /**
     * Construct a graph object using a string to integer index map computed from 'indexIdentifiers'. This method
     * is a first version of the 'generateFullGraph' method, and was used for testing purposes on a small sample of
     * the original data file (together with 'indexIdentifiers').
     * @param indices A string to integer map representing an integer indexing of the contig identifiers.
     * @param filename The name/location of the data file (used for making edges).
     * @return A Graph object representation of the data file.
     */
    public static Graph generateGraph(Map<String, Integer> indices, String filename) {
        System.out.println("Generating vertices from indices...");
        Graph g = new Graph(indices.size());
        System.out.println("Vertices generated.");
        FileReader fileReader;
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return g;
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        String[] tokens;
        int indexCounter = 0;
        System.out.println("Generating edges from file...");
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) != null) {
                    tokens = line.split("\t");
                    g.addEdge(indices.get(tokens[0]), indices.get(tokens[1]));
                } else break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return g;
    }

    /**
     * Generate a Graph object from the full data set using the index map from 'indexAllIdentifiers'. This method
     * is the final version of the 'generateGraph' method and is used to construct a graph representation of the full
     * data set. Like 'indexAllIdentifiers', it reads from the filtered chunks (the original data that has been split
     * into smaller parts and filtered of unnecessary data).
     * @param indices A string to integer index map representing the integer indexing of the contig identifiers.
     * @return A Graph object representation of the data set.
     */
    public static Graph generateFullGraph(Map<String, Integer> indices) {
        Graph g = new Graph(indices.size());
        String line;
        String[] tokens;
        for (int i = 0; i < 641; i++) {
            try {
                FileReader fileReader = new FileReader("res/splits/chunkF" + String.format("%04d", i));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while (true) {
                    if ((line = bufferedReader.readLine()) != null) {
                        tokens = line.split(DELIMITER);
                        // Add an edge between the integer representations of the first and second column of the data
                        g.addEdge(indices.get(tokens[0]), indices.get(tokens[1]));
                    } else break;
                }
                fileReader.close();
                bufferedReader.close();

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            double percentage = ((double) i) / 640;
            CustomWriter.updateProgress(percentage);
        }
        return g;
    }
}
