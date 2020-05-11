import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CustomWriter {

    public static <K, V> void writeMapToFile(Map<K, V> map, int entriesToWrite, String filename, String header) {

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

    public static <T> void writeListToFile(List<T> list, int entriesToWrite, String filename, String header) {

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

    public static void testDuration(long start, long end) {
        long duration = TimeUnit.NANOSECONDS.toMillis(end - start);
        System.out.println("Time elapsed: " + duration + " milliseconds.");
    }
}
