import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Handles printing progress and durations of tests to the standard output stream, and contains methods for writing
 * data structures to a text file (essentially unnecessarily long toString methods). The purpose of writing the data
 * to text is just to have a way of storing test results.
 */
public class CustomWriter {

    /**
     * Write a map with entries and values to a text file with one line per entry (key-value pair).
     * @param map Some object implementing the Map interface.
     * @param filename Name of the text file to create or overwrite.
     * @param header First line of the text file.
     * @param <K> Type parameter of the map keys.
     * @param <V> Type parameter of the map values.
     */
    public static <K, V> void writeMapToFile(Map<K, V> map, String filename, String header) {

        FileWriter fileWriter;
        BufferedWriter out;

        try {
            fileWriter = new FileWriter("res/results/" + filename + ".txt");
            out = new BufferedWriter(fileWriter);
            int count = 0;
            out.write(header + "\n");
            for (Map.Entry<K, V> entry : map.entrySet()) {
                out.write(entry.getKey().toString() + ":" + entry.getValue().toString() + "\n");
                count++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a list with entries to a text file with one line per list item.
     * @param list Some object implementing the List interface.
     * @param filename Name of the file to create or overwrite.
     * @param header First line of the file.
     * @param <T> Type parameter of the list items.
     */
    public static <T> void writeListToFile(List<T> list, String filename, String header) {

        FileWriter fileWriter;
        BufferedWriter out;

        try {
            fileWriter = new FileWriter("res/results/" + filename + ".txt");
            out = new BufferedWriter(fileWriter);
            int count = 0;
            out.write(header + "\n");
            ListIterator<T> iter = list.listIterator();
            while (iter.hasNext()) {
                int index = iter.nextIndex();
                T elem = iter.next();
                out.write(index + ":" + elem.toString() + "\n");
                count++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print and update a progress bar in standard output stream. Just to have a nice visual representation of
     * how far a test has progressed. Looks like this: [...        ]
     * @param progressPercentage A double between 0 and 1 represeting the progress percentage.
     */
    public static void updateProgress(double progressPercentage) {
        final int width = 50;
        System.out.print("\r[");
        int i = 0;
        for (; i < (int) (progressPercentage * width); i++) {
            System.out.print(".");
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
    }

    /**
     * Print the time elapsed to the standard output stream.
     * @param start A long representing the start time in nanoseconds.
     * @param end A long representing the end time in nanoseconds.
     */
    public static void testDuration(long start, long end) {
        long duration = TimeUnit.NANOSECONDS.toMillis(end - start);
        System.out.println("Time elapsed: " + duration + " milliseconds.");
    }
}
