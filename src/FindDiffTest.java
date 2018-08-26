import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class FindDiffTest {

    @org.junit.Test
    public void findOddTimeNumber1() {
        FindDiff sol = new FindDiff();
        try {
            GenerateFile.newTestFile("bigNumber.file", 10, new ArrayList<>());
            String res = sol.findOddTimeNumber("bigNumber.file");
            fail();
        }
        catch (Exception e) {
            assertEquals("File does not contains any odd-time elements",e.getMessage());
        }
    }

    @org.junit.Test
    public void findOddTimeNumber2() {
        FindDiff sol = new FindDiff();
        try {
            GenerateFile.newTestFile("bigNumber.file", 10,
                    new ArrayList<>(Arrays.asList("111111111111111111111111111111111111111111111111111111111111111",
                            "222222222222222222222222222222222222222222222222")));
            String res = sol.findOddTimeNumber("bigNumber.file");
            fail();
        }
        catch (Exception e) {
            assertEquals("File contains more then one odd-time element",e.getMessage());
        }
    }

    @org.junit.Test
    public void findOddTimeNumber3() {
        FindDiff sol = new FindDiff();
        try {
            GenerateFile.newTestFile("bigNumber.file", 10,
                    new ArrayList<>(Arrays.asList("111111111111111111111111111111111111111111111111111111111111111")));
            String res = sol.findOddTimeNumber("bigNumber.file");
            assertEquals("111111111111111111111111111111111111111111111111111111111111111", res);
        }
        catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            fail();
        }
    }

    /**
     * Test ~1Gb file search
     */
    @org.junit.Test
    public void findOddTimeNumber4() {
        FindDiff sol = new FindDiff();
        try {
            GenerateFile.newTestFile("bigNumber.file", 20000000,
                    new ArrayList<>(Arrays.asList("111111111111111111111111111111111111111111111111111111111111111")));
            String res = sol.findOddTimeNumber("bigNumber.file");
            assertEquals("111111111111111111111111111111111111111111111111111111111111111", res);
        }
        catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            fail();
        }
    }
}