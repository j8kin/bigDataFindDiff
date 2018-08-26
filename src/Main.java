import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        // generate file with big numbers
        try (PrintWriter out = new PrintWriter("filename.txt")) {
            int j = 0;
            //while (j < 10000000) {
            while (j < 10) {
                int numLength = (int) (Math.random() * 50 + 1);
                String textNumber = "";
                for (int i = 0; i < numLength; i++) {
                    textNumber += Integer.toString((int) (Math.random() * 10));
                }
                out.println(textNumber);
                j++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String homeDirectory = System.getProperty("user.home");
        Process process;
        try {
            process = Runtime.getRuntime()
                    .exec("cmd.exe /c copy filename.txt+filename.txt bigNumber.file");
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}