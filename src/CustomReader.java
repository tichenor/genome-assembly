import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CustomReader {

    public static int lineCount(String filename) throws IOException {
        // Probably only works for ASCII or UTF-8 character encoded files
        // Note: This can easily be done using the word count Unix command with newline option: wc -l
        long start = System.nanoTime();
        // Testing a larger buffer size on the BufferedInputStream resulted in faster runtime (default is 8 kB)
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename), 1024*64)) {
            byte[] b = new byte[1024]; // For some reason, 1024 (1 kB) seems very fast
            int readChars = is.read(b); // Read the next 1024 bytes
            if (readChars == -1) { // .read returns -1 if end of file is reached
                is.close();
                return 0;
            }

            int lineCount = 0;
            // Main body of the function; check for line breaks in the byte buffer
            while (readChars == 1024) {
                for (int i = 0; i < 1024;) {
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
            long end = System.nanoTime();
            long dur = TimeUnit.NANOSECONDS.toMillis((end - start));
            System.out.println("Line count took " + dur + " ms.");
            is.close();
            return lineCount == 0 ? 1 : lineCount;
        }
    }

    public static void loadAndAssembleGraph(String filename) {

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return; // fileReader is null, we don't want to continue
        }

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        String[] tokens;

        int testCounter = 0;
        int printVal = 0;
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) != null) {
                    tokens = line.split("\t");


//                    String fstId = tokens[0];
//                    String sndId = tokens[1];
//                    float simFrac = Float.parseFloat(tokens[3]);
//                    int fstStart = Integer.parseInt(tokens[5]);
//                    int fstEnd = Integer.parseInt(tokens[6]);
//                    int fstLength = Integer.parseInt(tokens[7]);
//                    boolean isReversed = !tokens[7].equals("0"); // false if 0, true otherwise (1)
//                    int sndStart = Integer.parseInt(tokens[9]);
//                    int sndEnd = Integer.parseInt(tokens[10]);
//                    int sndLength = Integer.parseInt(tokens[11]);



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
        return;
    }

}
