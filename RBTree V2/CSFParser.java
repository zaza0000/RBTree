package RBTree;

import java.io.*;
import java.util.*;

import javax.json.*;
import javax.json.stream.*;

public class CSFParser {
    public static Map<String, List<String>> process(String path) throws FileNotFoundException {
        Map<String, List<String>> HeaderData = new HashMap<>();
        File CSFDir = new File(path);
        ArrayList <File> filenames = new ArrayList<>();
        for(File File: CSFDir.listFiles()){
            int len = File.getName().length();
            if (File.getName().substring(len - 4, len).equals(".csf"))
                filenames.add(File);
        }
        System.out.println(filenames.size()+" CSF found.");
        if(filenames.size() == 0)
            return HeaderData;

        JsonParserFactory parserFactory = Json.createParserFactory(null);
        int index = 1;
        for(File File: filenames){
            //System.out.println("File "+index++);
            boolean hasFragments = true;
            String epoch = "0.0";
            JsonParser csfParser = parserFactory.createParser(new FileReader(File));
            JsonParser.Event E = csfParser.next();
            while(csfParser.hasNext() && hasFragments){
                if((E = csfParser.next()).equals(JsonParser.Event.KEY_NAME)) {// IF the next state is a key string
                    String key1 = csfParser.getString();
                    if(key1.equals("Header")){      // get time
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
                        HeaderData.put(File.getName(), start_end);
                        //System.out.println(File.getName());
                        break;
                    }
                }
            }       // each fragments

            csfParser.close();
        }       // each file

        return HeaderData;
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
        Map<String, List<String>> test = process(path);

        //printlist(test);
    }
}
