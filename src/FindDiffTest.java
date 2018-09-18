import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FindDiffTest {

    @org.junit.Test
    public void findOddTimeNumber1() {
        FindDiff sol = new FindDiff();
        try {
            GenerateFile.newTestFile("bigNumber.file", 10, new ArrayList<>());
            Integer res = sol.findOddTimeNumber("bigNumber.file"); // exception should occurs
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
                    new ArrayList<>(Arrays.asList("1111111111", "22222222")));
            Integer res = sol.findOddTimeNumber("bigNumber.file");
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
                    new ArrayList<>(Collections.singletonList("1111111111")));
            Integer res = sol.findOddTimeNumber("bigNumber.file");
            assertEquals(1111111111, (int) res);
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
            GenerateFile.newTestFile("bigNumber.file", 80000000,
                    new ArrayList<>(Collections.singletonList("1111111111")));
            Integer res = sol.findOddTimeNumber("bigNumber.file");
            assertEquals(1111111111, (int) res);
        }
        catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            fail();
        }
    }

    /**
     * Test ~1Gb file search multi-thread
     */
    @org.junit.Test
    public void findOddTimeNumber5() {
        try {
            GenerateFile.newTestFile("bigNumber.file", 80000000,
                    new ArrayList<>(Collections.singletonList("1111111111")));
            var sol = new FindDiffMultiThread();
            var res = sol.findOddTimeNumber("bigNumber.file", 5);
            assertEquals("1111111111", res);
        }
        catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            fail();
        }
    }
}