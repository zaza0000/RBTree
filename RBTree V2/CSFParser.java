package RBTree;

import com.sun.org.apache.bcel.internal.generic.GOTO;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class CSFParser {
    public static class IndexingData{
        public static Map<String, List<String>> HeaderData = new HashMap<>();
        public static Map<String, Map<String, List<Integer>>> ChannelIndex = new HashMap<>();
    }

    public static IndexingData process(String path) throws IOException {
        IndexingData InD = new IndexingData();
        File CSFDir = new File(path);
        ArrayList <File> filenames = new ArrayList<>();
        for(File File: CSFDir.listFiles()){
            int len = File.getName().length();
            if (File.getName().substring(len - 4, len).equals(".csf"))
                filenames.add(File);
        }
        System.out.println(filenames.size()+" CSF found.");
        if(filenames.size() == 0)
            return InD;

        JsonParserFactory parserFactory = Json.createParserFactory(null);
        int index = 1;
        for(File File: filenames){
            //System.out.println(File.getName());
            //System.out.println("File "+index++);
            boolean hasFragments = true;
            int numberOfChannels = 0;
            int curIndex = 0;
            String epoch = "0.0";
            JsonParser csfParser = parserFactory.createParser(new FileReader(File));
            JsonParser.Event E = csfParser.next();
            while(csfParser.hasNext() && hasFragments){
                if((E = csfParser.next()).equals(JsonParser.Event.KEY_NAME)) {// IF the next state is a key string
                    String key1 = csfParser.getString();
                    //System.out.println(key1);
                    //System.out.println(csfParser.getLocation());

                    if(key1.equals("Header")){      // get time
                        //System.out.println(key1);
                        List<String> start_end = new ArrayList<>();
                        while(!(E = csfParser.next()).equals(JsonParser.Event.END_OBJECT)){
                            if(E.equals(JsonParser.Event.KEY_NAME)){
                                String key2 = csfParser.getString();
                                if(key2.contains("FragmentNumber_")){
                                    String fragStartDate = "", fragStartTime = "";
                                    while(!(E = csfParser.next()).equals(JsonParser.Event.END_OBJECT)) {            // WHILE the fragment object has not ended DO
                                        if (E.equals(JsonParser.Event.KEY_NAME))
                                            switch (csfParser.getString()) {
                                                case "startDate":
                                                    E = csfParser.next();
                                                    fragStartDate = csfParser.getString();
                                                    break;
                                                case "startTime":
                                                    E = csfParser.next();
                                                    fragStartTime = csfParser.getString();
                                                    break;
                                                case "epoch":
                                                    E = csfParser.next();
                                                    epoch = csfParser.getString();
                                                    break;
                                            }
                                    }
                                    //System.out.print("Start: "+fragStartDate+" "+fragStartTime);
                                    //System.out.println(", duration: "+epoch);
                                    //String fragStart = fragStartDate + "," + fragStartTime;
                                    start_end.add(fragStartDate+","+fragStartTime+","+epoch);
                                }
                            }
                        }
                        InD.HeaderData.put(File.getName(), start_end);
                        //System.out.println(File.getName());
                        //break;
                    }
                    else if(key1.equals("channelList")){
                        E = csfParser.next();
                        String[] channelList = null;
                        if(E.equals(JsonParser.Event.VALUE_STRING)) {
                            channelList = csfParser.getString().replaceFirst("\\[", "")
                                    .replaceFirst("\\]", "").replace(" ","")
                                    .split(",");
                        }
                        String[] location = csfParser.getLocation().toString().split(",");
                        int line = Integer.parseInt(location[0].split("=")[1]);
                        line += 4;      // Fragment 1, the index of the first channel data
                        Map<String, List<Integer>> channelIndex = new HashMap<>();
                        for(String c:channelList){
                            List<Integer> temp = new ArrayList<>();
                            temp.add(line);
                            temp.add((line+channelList.length+2));
                            channelIndex.put(c, temp);
                            line++;
                        }
                        InD.ChannelIndex.put(File.getName(), channelIndex);
                        break;
                    }
                }
            }       // each fragments
            csfParser.close();
        }       // each file

        return InD;
    }

    public static void printlist(Map<String, List<String>> test){
        for(String key:test.keySet()){
            System.out.println(key+":");
            List<String> header = test.get(key);
            Iterator<String> it = header.iterator();
            while(it.hasNext()){
                System.out.print(it.next()+"  ");
            }
            System.out.println();
            System.out.println("------");
        }
    }

    public static void main(String[] args) throws IOException{
        System.out.println("CSFParser:");
        String path = "/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/Insular-Sz1-CSF";
        IndexingData test = process(path);

        //Stream<String> lines = Files.lines(Paths.get("/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/Insular-Sz1-CSF/Insular-CSF-0.csf"));
        //lines.close();
        //System.out.println();

        //printlist(test);
    }
}
