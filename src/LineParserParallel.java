import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LineParserParallel {

    private static final int KILOBYTE = 1024;
    private static final int MEGABYTE = 1024 * KILOBYTE;
    private static final String lineFeed = "\n";
    private static final String delimiter = "\t";

    private final String filenamePrefix;
    private final String targetDir;
    private final int numFiles;
    private final int numThreads;
    private ExecutorService threadPool;
    private AtomicInteger counter = new AtomicInteger(0);
    public Set<String> identifiers;
    public Hashtable<String, Integer> exclusions;


    class LineParseTask implements Runnable {

        private String targetDir;
        private int fileIndex;

        public LineParseTask(String targetDir, int fileIndex) {
            this.targetDir = targetDir;
            this.fileIndex = fileIndex;
        }

        private void doSomethingWithLine(String line) {

            String[] fields = line.split(delimiter);
            // identifiers.add(fields[0]);
            // identifiers.add(fields[1]);


        }

        @Override
        public void run() {
            String line;
            File file = new File(targetDir + filenamePrefix + String.format("%04d", fileIndex));
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                while ((line = br.readLine()) != null) {
                    doSomethingWithLine(line);
                }
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LineParserParallel(String targetDir, String filenamePrefix, int numFiles, int numThreads) {
        this.targetDir = targetDir;
        this.filenamePrefix = filenamePrefix;
        this.numFiles = numFiles;
        this.numThreads = numThreads;
        identifiers = Collections.synchronizedSet(new HashSet<>());
    }

    public void parseFiles() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numFiles; i++) {
            threadPool.submit(new LineParseTask(targetDir, i)); // File index starts at 0
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(60L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Happens if the thread pool is interrupted while waiting for threads to finish.
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LineParserParallel lpp = new LineParserParallel("res/splits/", "chunk", 641, 8);
        long start = System.nanoTime();
        try {
            lpp.parseFiles();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long dur = TimeUnit.NANOSECONDS.toMillis((end - start));
        System.out.println("Parse took " + dur + " ms.");
        System.out.println("Test found " + lpp.identifiers.size() + " identifiers.");

    }

}
