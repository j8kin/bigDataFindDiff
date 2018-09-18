import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class FileReadMultiThread implements Runnable {

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
        System.out.println("Reading the channel: " + _startLocation + ":" + _size);

        //allocate memory
        ByteBuffer buff = ByteBuffer.allocate(_size);

        //Read file chunk to RAM
        try {
            _channel.read(buff, _startLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //chunk to String
        var line = new StringBuffer();
        for (long i = 0; i < buff.limit(); i++)
        {
            var ch = ((char) buff.get());
            // todo: first number should be stored separatly because it could be a second half of the number
            if(ch=='\r'){
                // now we get all number lets try to find it in Set (try to delete) and if it is not in Set add
                //todo: probably _listOfDiff need to be Synchronized (!?)
                if (!_listOfDiff.remove(line.toString())) {
                    _listOfDiff.add(line.toString());
                }
                // purge line
                line=new StringBuffer();
            }else{
                line.append(ch);
            }
        }
        // todo: if line contains something we need to work with it in future this is half of file
        StringBuilder string_chunk = new StringBuilder();

        System.out.println("Done Reading the channel: " + _startLocation + ":" + _size);

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
        ExecutorService executor = Executors.newFixedThreadPool(threadNumb);

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

        // wait all threads complete
        while (Thread.activeCount() > 1) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException ie) {
            }
        }

        //Tear Down
        if (_listOfDiff.size() > 1)
            throw new Exception("File contains more then one odd-time element");

        if (_listOfDiff.size() == 0)
            throw new Exception("File contains more then one odd-time element");

        return _listOfDiff.iterator().next();
    }
}
