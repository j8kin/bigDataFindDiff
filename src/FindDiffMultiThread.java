import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

class FileReadMultiThread implements Runnable {
    public static int nActiveThreads = 0;
    public static HashMap<Integer, String> tail = new HashMap<>();
    public static HashMap<Integer, String> head = new HashMap<>();

    private static Object mutex = new Object();
    private FileChannel _channel;
    private long _startLocation;
    private int _size;
    private int _sequence_number;
    private Set<String> _listOfDiff;

    public FileReadMultiThread(long loc, int size, FileChannel chnl, int sequence, Set<String> listOfDiff)
    {
        _startLocation = loc;
        _size = size;
        _channel = chnl;
        _sequence_number = sequence;
        _listOfDiff = listOfDiff;
    }

    @Override
    public void run()
    {
        synchronized(mutex) {
            nActiveThreads++;
        }
        System.out.println("Reading the channel: " + _startLocation + ":" + _size);

        //allocate memory
        ByteBuffer buff = ByteBuffer.allocate(_size);

        //Read file chunk to RAM
        try {
            _channel.read(buff, _startLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        buff.rewind();
        /*
        var lines = StandardCharsets.UTF_8.decode(buff).toString().split("[\\r\\n]+");
        synchronized(mutex) {
            if (lines.length > 0 && lines[0] != null && head.get(_sequence_number) == null) {
                // this is start number store it
                head.put(_sequence_number, lines[0].replaceAll("(\\r|\\n)", ""));
                //System.out.println("Add start("+_sequence_number+"): " + lines[0]);
            }
            if (lines.length > 0 && tail.get(_sequence_number) == null) {
                // this is start number store it
                tail.put(_sequence_number, lines[lines.length-1].replaceAll("(\\r|\\n)", ""));
                //System.out.println("Add end("+_sequence_number+"): " + lines[lines.length-1]);
            }
        }

        for (var i = 1; i< lines.length-1;++i) {
            synchronized (mutex) {
                var number = lines[i].replaceAll("(\\r|\\n)", ""); //sometimes the last character could be \r or \n
                if (!_listOfDiff.remove(number)) {
                    _listOfDiff.add(number);
                    //System.out.println("Number of numbers in set: " + _listOfDiff.size());
                }
            }
        }
        */
        //chunk to String
        var line = new StringBuffer();
        for (long i = 0; i < _size; i++)
        {
            var ch = ((char) buff.get());
            if(ch=='\r'){
                // now we get all number lets try to find it in Set (try to delete) and if it is not in Set add
                synchronized(mutex) {
                    if (head.get(_sequence_number) == null) {
                        // this is start number store it
                        head.put(_sequence_number, line.toString().replaceAll("(\\r|\\n)", ""));
                    }
                    else {
                        var number = line.toString().replaceAll("(\\r|\\n)", "");
                        if (!_listOfDiff.remove(number)) {
                            _listOfDiff.add(number);
                            //System.out.println("Number of numbers in set: " + _listOfDiff.size());
                        }
                    }
                }
                // purge line
                line=new StringBuffer();
            }else{
                line.append(ch);
            }
        }


        System.out.println("Done Reading the channel: " + _startLocation + ":" + _size);
        synchronized(mutex) {
            if (tail.get(_sequence_number) == null && line.length() > 0) {
                // this is start number store it
                tail.put(_sequence_number, line.toString().replaceAll("(\\r|\\n)", ""));
            }
            nActiveThreads--;
        }
    }

}
public class FindDiffMultiThread {
    private Set<String> _listOfDiff = Collections.synchronizedSet(new HashSet<>());

    String findOddTimeNumber(String filename, int threadNumb) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(filename);
        FileChannel channel = fileInputStream.getChannel();
        long remaining_size = channel.size(); //get the total number of bytes in the file
        long chunk_size = remaining_size / threadNumb; //file_size/threads

        //thread pool
        var executor = Executors.newFixedThreadPool(threadNumb);
        int nThreads = 0;

        long start_loc = 0;//file pointer
        int i = 0; //loop counter
        while (remaining_size >= chunk_size) {
            //launches a new thread
            executor.execute(new FileReadMultiThread(start_loc, Math.toIntExact(chunk_size), channel, i, _listOfDiff));
            remaining_size = remaining_size - chunk_size;
            start_loc = start_loc + chunk_size;
            i++;
        }

        //load the last remaining piece
        executor.execute(new FileReadMultiThread(start_loc, Math.toIntExact(remaining_size), channel, i, _listOfDiff));

        Thread.sleep(10000);
        // wait all threads complete
        while (FileReadMultiThread.nActiveThreads > 1) {
            //System.out.println("Number of threads: " + FileReadMultiThread.nActiveThreads);
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ie) {
            }
        }

        System.out.print("Elements in Hash: ");
        for (String s : _listOfDiff) {
            System.out.print(" '"+s+"'");
        }
        System.out.print("\n");

        //all threads complete now lets merge tails and heads and remove existing from _listOfDiff
        for (int j=0; j<threadNumb; ++j) {
            String additionalN = "";
            if (FileReadMultiThread.head.containsKey(j)) {
                if (FileReadMultiThread.tail.containsKey(j-1)){
                    additionalN = FileReadMultiThread.tail.get(j-1) + FileReadMultiThread.head.get(j);
                }
                else {
                    additionalN = FileReadMultiThread.head.get(j);
                }
            }
            //System.out.println("Gluck #1: " + j + " : " + additionalN);
            if (!_listOfDiff.remove(additionalN)) {
                _listOfDiff.add(additionalN);
            }
        }
        // add the last element
        if (!_listOfDiff.remove(FileReadMultiThread.tail.get(threadNumb-1))) {
            _listOfDiff.add(FileReadMultiThread.tail.get(threadNumb-1));
        }
        /*
        for (int j=0; j < threadNumb; j++) {
            String addNumber = "";
            if (FileReadMultiThread.head.containsKey(j)) {
                if (FileReadMultiThread.tail.containsKey(j-1)){
                    addNumber = FileReadMultiThread.tail.get(j-1)+FileReadMultiThread.head.get(j);
                }
                else {
                    addNumber = FileReadMultiThread.head.get(j);
                }
            }
            addNumber = addNumber.replaceAll("(\\r|\\n)", "");
            if (!_listOfDiff.remove(addNumber)) {
                System.out.println("Number of numbers in set: " + _listOfDiff.size());
                System.out.println("Number add: " + addNumber + " j=" + j);
                _listOfDiff.add(addNumber);
                System.out.println("Number of numbers in set: " + _listOfDiff.size());
            }
        }
        //remove the last number
        if (!_listOfDiff.remove(FileReadMultiThread.tail.get(threadNumb-1).replaceAll("(\\r|\\n)", ""))) {
            _listOfDiff.add(FileReadMultiThread.tail.get(threadNumb-1).replaceAll("(\\r|\\n)", ""));
            System.out.println("Number of numbers in set: " + _listOfDiff.size());
        }
        */
        System.out.print("Elements in Hash: ");
        for (String s : _listOfDiff) {
            System.out.print(" '"+s+"'");
        }
        System.out.print("\n");

        System.out.println("Number of elements: "+_listOfDiff.size());
        //Tear Down
        if (_listOfDiff.size() > 1)
            throw new Exception("File contains more then one odd-time element");

        if (_listOfDiff.size() == 0)
            throw new Exception("File contains more then one odd-time element");

        return _listOfDiff.iterator().next();
    }
}
