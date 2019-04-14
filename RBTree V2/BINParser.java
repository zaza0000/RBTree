package RBTree;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class BINParser {

    public static class IndexingData{
        public static Map<String, List<String>> HeaderData = new HashMap<>();
        public static Map<String, Map<String, List<Long>>> ChannelIndex = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
        //System.out.println("Bin Parser");
        String folderName = "/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/output2";
        IndexingData test = process(folderName);

    }

    public static IndexingData process(String path) throws IOException {
        IndexingData InD = new IndexingData();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        //System.out.println(listOfFiles.length);
        for (int i = 0; i < listOfFiles.length; i++) {
            if(listOfFiles[i].isDirectory()) {
                for (File f : listOfFiles[i].listFiles()) {
                    String filePath = f.getPath();
                    RandomAccessFile readStore = new RandomAccessFile(filePath, "rw");
                    String fileName = filePath.replaceFirst(path+"/","");

                    byte[] temp = new byte[8];      // size of the data part.
                    readStore.readFully(temp);
                    long dataLength = ByteBuffer.wrap(temp).getLong();
                    //System.out.println(dataLength);
                    readStore.seek(dataLength);
                    temp = new byte[4];     // size if the header part.
                    readStore.readFully(temp);
                    int headerLength = ByteBuffer.wrap(temp).getInt();
                    //System.out.println(headerLength);
                    byte[] headerData = new byte[headerLength];
                    readStore.readFully(headerData);

                    JSONObject jsonObj = new JSONObject(new String(headerData, "utf-8"));

                    JSONObject header =jsonObj.getJSONObject("Header");
                    Iterator iterator = header.keys();
                    String key = (String) iterator.next();
                    JSONObject fragment = header.getJSONObject(key);

                    List<String> start_end = new ArrayList<>();
                    start_end.add(fragment.getString("startDate")+","
                            +fragment.getString("startTime")+","+fragment.getDouble("epoch"));
                    InD.HeaderData.put(fileName, start_end);

                    jsonObj = jsonObj.getJSONObject("ChannelIndex");
                    InD.ChannelIndex.put(fileName, getChannelIndex(jsonObj));

                    readStore.close();
                }
            }
        }

        return InD;
    }

    private static Map<String, List<Long>> getChannelIndex(JSONObject jsonObj){
        Map<String, List<Long>> channelIndex = new HashMap<>();
        for(String ch: jsonObj.keySet()){
            JSONArray jsonArr = jsonObj.getJSONArray(ch);
            Long start = jsonArr.getLong(0);
            Long end = jsonArr.getLong(1);
            List<Long> start_end = new ArrayList<>();
            start_end.add(start);
            start_end.add(end);
            channelIndex.put(ch, start_end);
        }
        return channelIndex;
    }
}
