import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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
    public Map<Integer, Set<Integer>> exclusions;

    /**
     * Base class for a runnable task to parse each line of a specific file. To be run by another thread.
     * The format for the filename needs to be adjusted if the directory or any filenames are modified.
     */
    abstract class LineParseTask implements Runnable {

        protected int fileIndex;
        protected int linePosition;

        public LineParseTask(int fileIndex) {
            this.fileIndex = fileIndex;
            linePosition = 0;
        }

        protected abstract void doSomethingWithLine(String line);

        @Override
        public void run() {
            String line;
            File file = new File(targetDir + filenamePrefix + String.format("%04d", fileIndex));
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                while ((line = br.readLine()) != null) {
                    linePosition++;
                    doSomethingWithLine(line);
                }
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Runnable task that finds exclusions for contig overlaps. If one contig is contained in the other, the task
     * stores the line position (data point) in a HashMap. The key is the file index and the value is a set of line
     * positions (integers).
     */
    class FindExclusionTask extends LineParseTask {

        public FindExclusionTask(int fileIndex) {
            super(fileIndex);
        }

        protected void doSomethingWithLine(String line) {
            String[] fields = line.split(delimiter);
            // Check if one contig is contained in the other by checking if the overlap is the whole contig.
            if (fields[5].equals("0") && fields[6].equals(fields[7])
                    || fields[9].equals("0") && fields[10].equals(fields[11])) {
                // First contig contained in second or vice versa, note the position of the exclusion.
                exclusions.computeIfAbsent(fileIndex, k -> new HashSet<>()); // Make new set if there is none yet.
                exclusions.get(fileIndex).add(linePosition); // Add the line position to the set of exclusions for the file.
            }
        }
    }

    /**
     * Parse multiple text files using one or more parallel threads, possibly storing results.
     *
     * @param targetDir Target directory relative to the project root.
     * @param filenamePrefix If the files have a common beginning, e.g. "file01, file02, ...".
     * @param numFiles The number of files to process.
     * @param numThreads The number of threads to use.
     */
    public LineParserParallel(String targetDir, String filenamePrefix, int numFiles, int numThreads) {
        this.targetDir = targetDir;
        this.filenamePrefix = filenamePrefix;
        this.numFiles = numFiles;
        this.numThreads = numThreads;
        // identifiers = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * The method that does the parsing.
     */
    public void findExclusions() {
        exclusions = Collections.synchronizedMap(new HashMap<>()); // Initialize storage for exclusions.
        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads); // Create thread pool
        for (int i = 0; i < numFiles; i++) {
            // Submit a runnable task for each file.
            threadPool.submit(new FindExclusionTask(i)); // File index starts at 0
        }
        threadPool.shutdown(); // Make the thread pool not accept any further tasks.
        try {
            threadPool.awaitTermination(60L, TimeUnit.SECONDS); // Wait for tasks to finish.
        } catch (InterruptedException e) {
            // Happens if the thread pool is interrupted while waiting for threads to finish.
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LineParserParallel lpp = new LineParserParallel("res/splits/", "chunk", 10, 2);
        long start = System.nanoTime();
        lpp.findExclusions();
        long end = System.nanoTime();
        long dur = TimeUnit.NANOSECONDS.toMillis((end - start));
        System.out.println("Parse took " + dur + " ms.");
        for (Map.Entry<Integer, Set<Integer>> entry : lpp.exclusions.entrySet()) {
            System.out.println("Found " + entry.getValue().size() + " exclusions in file " + entry.getKey());
        }
    }

}
