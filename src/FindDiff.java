import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

/**
 * This class perform search on a enormous files which have the following format:
 *  1. Each line contain only one number (it could be bigger then int)
 *  2. All numbers except the only one present even times in file
 *  3. Only one number present odd time
 *
 *  This version implements single thread algorithm.
 *  If this huge file stored in Solid HDD multi-thread access will decrement total analysis time
 *    since HDD head will take an additional time to move to a new position for each thread.
 *  If SSD drive is used then multi-thread algorithm is preferred.
 */
class FindDiff {

    /**
     * oddNumber contains current set of numbers read from file
     *
     * Important Notice: HashSet is not thread-safe! Do not extend this algorithm for multi-threading
     */
    private HashSet<Integer> oddNumbers = new HashSet<>();

    /**
     * Find number which present odd times in file
     * @param filename name of the file to be analysed
     * @return odd number in file
     */
    Integer findOddTimeNumber(String filename) throws Exception  {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();

            while (line != null) {
                //first try to remove element from HashSet if it is not exist - add it
                if (!oddNumbers.remove(Integer.parseInt(line))) {
                    oddNumbers.add(Integer.parseInt(line));
                }
                line = br.readLine();
            }
        }

        if (oddNumbers.size() > 1)
        {
            throw new Exception("File contains more then one odd-time element");
        }
        if (oddNumbers.size() == 0)
        {
            throw new Exception("File does not contains any odd-time elements");
        }
        return oddNumbers.iterator().next(); // return first and actually the only one element from the hashSet
    }
}
