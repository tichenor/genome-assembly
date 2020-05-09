import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CustomReader {

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
                    if (b[i++] == '\n') {
                        ++lineCount;
                    }
                }
                readChars = is.read(b);
            }

            // Search the remaining bytes (should be less than 1024 at this point)
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (b[i] == '\n') {
                        ++lineCount;
                    }
                }
                readChars = is.read(b);
            }
            is.close();
            return lineCount == 0 ? 1 : lineCount;
        }
    }

    public static Map<String, Integer> indexIdentifiers(String filename) {
        Map<String, Integer> indices = new HashMap<>();
        FileReader fileReader = null;
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

    public static Map<String, Integer> indexAllIdentifiers() {
        Map<String, Integer> indices = new HashMap<>();
        FileReader fileReader = null;
        String line;
        String[] tokens;
        int indexCount = 0;
        for (int i = 0; i < 641; i++) {
            try {
                fileReader = new FileReader("res/splits/chunkF" + String.format("%04d", i));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while (true) {
                    if ((line = bufferedReader.readLine()) != null) {
                        tokens = line.split("\t");
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
            if (i % 10 == 0) {
                System.out.println(i + " chunks processed.");
            }

        }
        return indices;
    }

    public static Graph generateGraph(Map<String, Integer> indices, String filename) {
        System.out.println("Generating vertices from indices...");
        Graph g = new Graph(indices.size());
        System.out.println("Vertices generated.");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return g;
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = null;
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

    public static Graph generateFullGraph(Map<String, Integer> indices) {
        System.out.println("Generating vertices from indices...");
        Graph g = new Graph(indices.size());
        System.out.println("Vertices generated.");
        String line;
        String[] tokens;
        for (int i = 0; i < 641; i++) {
            try {
                FileReader fileReader = new FileReader("res/splits/chunkF" + String.format("%04d", i));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while (true) {
                    if ((line = bufferedReader.readLine()) != null) {
                        tokens = line.split("\t");
                        g.addEdge(indices.get(tokens[0]), indices.get(tokens[1]));
                    } else break;
                }
                fileReader.close();
                bufferedReader.close();

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (i % 10 == 0) {
                System.out.println(i + " chunks processed.");
            }
        }
        return g;
    }
}
