import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LineParserParallel {

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

    class HeheTask extends LineParseTask {

        public HeheTask(int fileIndex) {
            super(fileIndex);
        }

        @Override
        protected void doSomethingWithLine(String line) {
            String[] fields = line.split(delimiter);
            identifiers.add(fields[0]);
            identifiers.add(fields[1]);
        }
    }

    /**
     * Runnable task that finds exclusions for contig overlaps. If one contig is contained in the other, the task
     * stores the line position (data point) in a HashMap. The key is the file index and the value is a set of line
     * positions (integers). This class is mostly for testing as its not very efficient to simply store all exclusions
     * in a set.
     */
    class FindExclusionTask extends LineParseTask {

        public FindExclusionTask(int fileIndex) {
            super(fileIndex);
        }

        protected void doSomethingWithLine(String line) {
            String[] fields = line.split(delimiter);
            // Check if one contig is contained in the other by checking if the overlap is the whole contig.
            if (
                    (fields[5].equals("0") && fields[6].equals(fields[7])) // overlap is all of first contig
                    || (fields[9].equals("0") && fields[10].equals(fields[11])) // overlap is all of second contig
            ) {
                // First contig contained in second or vice versa, note the position of the exclusion.
                exclusions.computeIfAbsent(fileIndex, k -> new HashSet<>()); // Make new set if there is none yet.
                exclusions.get(fileIndex).add(linePosition); // Add the line position to the set of exclusions for the file.
            }
        }
    }

    /**
     * Runnable task that filters out any false overlaps (containments) and copies 'true' overlaps into a new text file.
     */
    class FilteredCopyTask extends LineParseTask {

        private PrintWriter writer;

        public FilteredCopyTask(int fileIndex) {
            super(fileIndex);
        }

        protected void doSomethingWithLine(String line) {
            String[] fields = line.split(delimiter);
            // Check if one contig is contained in the other by checking if the overlap is the whole contig.
            if (
                    (!fields[5].equals("0") || !fields[6].equals(fields[7])) // overlap is not all of first contig
                    && (!fields[9].equals("0") || !fields[10].equals(fields[11])) // overlap is not all of second contig
            ) {
                writer.println(line); // write line if overlap is not a containment
            }
        }

        @Override
        public void run() {
            String line;
            File sourceFile = new File(targetDir + filenamePrefix + String.format("%04d", fileIndex));
            File targetFile = new File(targetDir + filenamePrefix + "F" + String.format("%04d", fileIndex));
            try {
                targetFile.createNewFile(); //TODO: check if true/false; if new file was created
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileReader fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                writer = new PrintWriter(new FileWriter(targetFile));
                while ((line = br.readLine()) != null) {
                    linePosition++;
                    doSomethingWithLine(line);
                }
                br.close();
                fr.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
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
     * Find overlaps that are contaiments and mark the line position and file index where they are found. This method
     * is mostly for testing as it's not very efficient to store all 'false' overlaps.
     */
    public void findExclusions() {
        exclusions = Collections.synchronizedMap(new HashMap<>()); // Initialize storage for exclusions.
        threadPool = Executors.newFixedThreadPool(numThreads); // Initialize thread pool
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

    public void filterExclusionsAndCopy() {
        threadPool = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numFiles; i++) {
            threadPool.submit(new FilteredCopyTask(i));
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(60L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void hehe() {
        identifiers = Collections.synchronizedSet(new HashSet<>());
        threadPool = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numFiles; i++) {
            threadPool.submit(new HeheTask(i));
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(60L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LineParserParallel lpp = new LineParserParallel("res/splits/", "chunk", 641, 8);
        long start = System.nanoTime();
        lpp.hehe();
        long end = System.nanoTime();
        long dur = TimeUnit.NANOSECONDS.toMillis((end - start));
        System.out.println("Parse took " + dur + " ms.");
        System.out.println("Identifiers: " + lpp.identifiers.size());
//
//        for (Map.Entry<Integer, Set<Integer>> entry : lpp.exclusions.entrySet()) {
//            System.out.println("Found " + entry.getValue().size() + " exclusions in file " + entry.getKey());
//        }
    }

}
