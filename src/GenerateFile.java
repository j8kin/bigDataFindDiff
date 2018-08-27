import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * This class is for test purposes only and generate file for tests
 */
class GenerateFile {
     static void newTestFile(String filename, int nLines, ArrayList<String> additionalLines) {
         try (PrintWriter out = new PrintWriter("filename.txt")) {
             int j = 0;
             while (j < nLines) {
                 int numLength = (int) (Math.random() * 8 + 1);
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

         if (!additionalLines.isEmpty())
         {
             try (PrintWriter out = new PrintWriter("additional.file")) {
                 for(String elem: additionalLines) {
                     out.println(elem);
                 }
             } catch (FileNotFoundException e) {
                 e.printStackTrace();
             }
         }


         Process process;
         try {
             if (!additionalLines.isEmpty()){
                 process = Runtime.getRuntime()
                         .exec("cmd.exe /c copy /b filename.txt+filename.txt+additional.file " + filename);
             }
             else {
                 process = Runtime.getRuntime()
                         .exec("cmd.exe /c copy /b filename.txt+filename.txt " + filename);
             }
             StreamGobbler streamGobbler =
                     new StreamGobbler(process.getInputStream(), System.out::println);
             Executors.newSingleThreadExecutor().submit(streamGobbler);
             int exitCode = process.waitFor();
             assert exitCode == 0;
         } catch (IOException | InterruptedException e) {
             e.printStackTrace();
         }
     }
}
