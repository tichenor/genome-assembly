import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        final String filename = "res/Spruce_fingerprint_2017-03-10_16.48.olp.m4";
        int lineCount = -1;
        try {
            lineCount = CustomReader.lineCount(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(lineCount);
    }

}
