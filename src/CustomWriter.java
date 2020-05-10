import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class CustomWriter {

    public static <A, B> void writeMapToFile(Map<A, B> map, int entriesToWrite, String filename) {

        FileWriter fileWriter;
        BufferedWriter out;

        try {
            fileWriter = new FileWriter("res/results/" + filename + ".txt");
            out = new BufferedWriter(fileWriter);
            int count = 0;
            Iterator<Map.Entry<A, B>> iterator = map.entrySet().iterator();
            while (iterator.hasNext() && count < entriesToWrite) {
                Map.Entry<A, B> entry = iterator.next();
                out.write(entry.getKey().toString() + ":" + entry.getValue().toString() + "\n");
                count++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
