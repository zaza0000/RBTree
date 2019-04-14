package RBTree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RBTreeSearch {
    public static void main(String[] args) throws IOException{
        if(args.length < 3) {
            System.out.println("lack of parameters");
            return;
        }

        long begin = System.currentTimeMillis();

        String path = "/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/output/12345";
        //String path = "/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/Insular-Sz1-CSF";
        CSFParser.IndexingData fileData = CSFParser.process(path);

        RedAndBlackTree<String> tree=new RedAndBlackTree<>();
        tree.insertCSF(fileData.HeaderData, fileData.ChannelIndex);
        System.out.println();
        System.out.println("RBTreeInfo:");
        tree.print();
        tree.checkBlackNum();
        System.out.println();

        System.out.println("start data+time: "+args[0]);
        System.out.println("end data+time: "+args[1]);
        System.out.println();

        Map<String, Map<String, String>> hashmapIndexing = getIndexing(args[0], args[1], args[2], tree, path);
//        Set<String> set = hashmapIndexing.keySet();
//        for(String s:set){
//            System.out.println(s);
//            Map<String, String> temp = hashmapIndexing.get(s);
//            List<Map.Entry<String, String>> list=new ArrayList<>();
//            list.addAll(temp.entrySet());
//            RBTreeSearch.ValueComparator vc=new ValueComparator();
//            Collections.sort(list,vc);
//
//            for(Iterator<Map.Entry<String, String>> it=list.iterator();it.hasNext();)
//            {
//                System.out.println(it.next());
//            }
//        }

        LinkedHashMap <String, ArrayList <Double>> dataMap ;

        Set<String> set = hashmapIndexing.keySet();
        for(String fileAddress:set){
            dataMap = new LinkedHashMap<>();
            System.out.println(fileAddress);
            FileInputStream fs= new FileInputStream(fileAddress);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));

            Map<String, String> temp = hashmapIndexing.get(fileAddress);
            Set<String> channelSet = temp.keySet();
            for(String c: channelSet){
                dataMap.put(c, new ArrayList <Double>());
            }

            List<Map.Entry<String, String>> templist=new ArrayList<>();
            templist.addAll(temp.entrySet());
            RBTreeSearch.ValueComparator vc=new ValueComparator();
            Collections.sort(templist, vc);
//            for(Iterator<Map.Entry<String, String>> it=templist.iterator();it.hasNext();)
//            {
//                System.out.println(it.next());
//            }
            int iteration = 0;
            boolean flag = false;
            int currentLine = 0;
            while(iteration < 2) {
                for (Iterator<Map.Entry<String, String>> it = templist.iterator(); it.hasNext(); ) {
                    String[] s = it.next().toString().split("=");
                    String channelName = s[0];
                    String[] CI = s[1].split(",");
                    if(CI.length == 1)
                        flag = true;
                    Integer nextLine = Integer.parseInt(CI[iteration]);
                    for(; currentLine < nextLine-1; currentLine++)
                        br.readLine();
                    currentLine++;
                    String DataTemp = br.readLine().split("\":\"")[1].replaceFirst("\\[", "")
                            .replaceFirst("\\]", "").replace("\"", "");
                    Stream <String> D = Arrays.stream(DataTemp.split(","));
                    List<Double> channelData = D.map(Double::parseDouble).collect(Collectors.toList());

                    dataMap.get(channelName).addAll(channelData);
                    //System.out.println(dataMap.get(channelName));
                    D.close();
                }
                iteration++;
                if(flag == true)
                    break;
            }
            br.close();
            fs.close();
            System.out.println("File Data Processed");
        }

        long endTime=System.currentTimeMillis();
        System.out.println();
        System.out.println(endTime-begin+"ms");
        tree.clear();
    }

    public static Map<String, Map<String, String>> getIndexing(String start, String end, String channelList, RedAndBlackTree tree, String path){
        List l = tree.search(start, end);
        String[] channels = null;
        boolean needAll = false;
        if(channelList.equals("-ALL"))
            needAll = true;
        else
            channels = channelList.split(",");

        Map<String, Map<String, String>> LineNeedToFetch = new HashMap<>();
        Iterator<RedAndBlackTree.TreeNode> it = l.iterator();
        while(it.hasNext()){
            Map<String, String> temp = new HashMap<>();
            RedAndBlackTree.TreeNode tempTree = it.next();
            //System.out.println(tempTree.toString());
            String fileAddress = path+"/"+tempTree.getFileName();
            if(needAll == false){
                int check_frags = checkFrags(start, end, tempTree.fra2_sDate+","+tempTree.fra2_sTime);
                switch(check_frags){
                    case 0:         // only the second fragment
                        temp = getIndex1(0, channels, tempTree.getChannelsIndex());
                        break;
                    case 1:         // both
                        temp = getIndex1(1, channels, tempTree.getChannelsIndex());
                        break;
                    case 2:         // only the first fragment
                        temp = getIndex1(2, channels, tempTree.getChannelsIndex());
                        break;
                }

            }else{
                int check_frags = checkFrags(start, end, tempTree.fra2_sDate+","+tempTree.fra2_sTime);
                switch(check_frags){
                    case 0:         // only the second fragment
                        temp = getIndex2(0, tempTree.getChannelsIndex());
                        break;
                    case 1:         // both
                        temp = getIndex2(1, tempTree.getChannelsIndex());
                        break;
                    case 2:         // only the first fragment
                        temp = getIndex2(2, tempTree.getChannelsIndex());
                        break;
                }

            }

            LineNeedToFetch.put(fileAddress, temp);
        }

        return LineNeedToFetch;
    }

    private static Map<String, String>  getIndex1(int type, String[] channels, Map<String, List<Integer>> channelMap){
        Map<String, String> channelIndexingMap = new HashMap<>();
        if(type == 0){      // second
            for(String channel: channels){
                List<Integer> temp = channelMap.get(channel);
                String temp2 = "";
                temp2 += temp.get(1).toString();
                channelIndexingMap.put(channel, temp2);
            }
        }else if(type == 1){        //both
            for(String channel: channels){
                List<Integer> temp = channelMap.get(channel);
                String temp2 = "";
                temp2 += temp.get(0).toString()+",";
                temp2 += temp.get(1).toString();
                channelIndexingMap.put(channel, temp2);
            }
        }else{      //first
            for(String channel: channels){
                List<Integer> temp = channelMap.get(channel);
                String temp2 = "";
                temp2 += temp.get(0).toString();
                channelIndexingMap.put(channel, temp2);
            }
        }

        return channelIndexingMap;
    }

    private static Map<String, String> getIndex2(int type, Map<String, List<Integer>> channelMap){     // all channel
        Map<String, String> channelIndexingMap = new HashMap<>();
        Set<String> channels = channelMap.keySet();
        if(type == 0){      // second
            for(String channel: channels){
                List<Integer> temp = channelMap.get(channel);
                String temp2 = "";
                temp2 += temp.get(1).toString();
                channelIndexingMap.put(channel, temp2);
            }
        }else if(type == 1){        //both
            for(String channel: channels){
                List<Integer> temp = channelMap.get(channel);
                String temp2 = "";
                temp2 += temp.get(0).toString()+",";
                temp2 += temp.get(1).toString();
                channelIndexingMap.put(channel, temp2);
            }
        }else{      //first
            for(String channel: channels){
                List<Integer> temp = channelMap.get(channel);
                String temp2 = "";
                temp2 += temp.get(0).toString();
                channelIndexingMap.put(channel, temp2);
            }
        }

        return channelIndexingMap;
    }

    //make sure how many fragments need to be extracted from a specific CDF file  (supposed there are two frags in each file)
    //return 0, only the second frag
    //return 1, both frags
    //return 2, only the first frag
    public static int checkFrags(String start, String end, String frag2Start){
        int temp1 = compareString(start, frag2Start);
        int temp2 = compareString(end, frag2Start);
        if(temp1 >= 0)
            return 0;
        else if(temp2 > 0)
            return 1;
        else
            return 2;
    }


    private static int compareString(String s1, String s2){
        String[] temp = s1.split(",");
        String date1 = temp[0],
                time1 = temp[1];
        temp = s2.split(",");
        String date2 = temp[0],
                time2 = temp[1];

        temp = date1.split("\\.|:");        // s1.date
        int day1 = Integer.parseInt(temp[0]),
                month1 = Integer.parseInt(temp[1]),
                year1 = Integer.parseInt(temp[2]);
        temp = date2.split("\\.|:");        // s2.date
        int day2 = Integer.parseInt(temp[0]),
                month2 = Integer.parseInt(temp[1]),
                year2 = Integer.parseInt(temp[2]);

        if(year1 < year2)
            return -1;
        else if(year1 > year2)
            return 1;
        if(month1 < month2)
            return -1;
        else if(month1 < month2)
            return 1;
        if(day1 < day2)
            return -1;
        else if(day1 > day2)
            return 1;
        return time1.compareTo(time2);
    }

    private static class ValueComparator implements Comparator<Map.Entry<String,String>>
    {
        public int compare(Map.Entry<String,String> m,Map.Entry<String,String> n)
        {
            Long mVal = Long.parseLong(m.getValue().split(",")[0]);
            Long nVal = Long.parseLong(n.getValue().split(",")[0]);
            //System.out.println(mVal);
            //System.out.println(mVal+" "+nVal);
            if(mVal < nVal) {
                return -1;
            }
            else if(mVal > nVal) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }

}
