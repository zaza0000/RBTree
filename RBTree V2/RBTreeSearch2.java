package RBTree;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RBTreeSearch2 {
    public static void main(String[] args) throws IOException{
        if(args.length < 3) {
            System.out.println("lack of parameters");
            return;
        }

        long begin = System.currentTimeMillis();

        String path = "/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/output3";
        //String path = "/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/Insular-Sz1-CSF";
        BINParser.IndexingData fileData = BINParser.process(path);

        RedAndBlackTree<String> tree=new RedAndBlackTree<>();
        tree.insertCSF2(fileData.HeaderData, fileData.ChannelIndex);
        System.out.println();
        System.out.println("RBTreeInfo:");
        tree.print();
        tree.checkBlackNum();
        System.out.println();

        System.out.println("start data+time: "+args[0]);
        System.out.println("end data+time: "+args[1]);
        System.out.println();

        Map<String, Map<String, String>> hashmapIndexing = getIndexing(args[0], args[1], args[2], tree, path);
        LinkedHashMap <String, ArrayList <Double>> dataMap ;

        Set<String> set = hashmapIndexing.keySet();
        for(String file:set){
            dataMap = new LinkedHashMap<>();
            System.out.println(file);
            Map<String, String> temp = hashmapIndexing.get(file);
            Set<String> channelSet = temp.keySet();
            for(String c: channelSet){
                dataMap.put(c, new ArrayList <Double>());
            }

            List<Map.Entry<String, String>> list=new ArrayList<>();
            list.addAll(temp.entrySet());
            RBTreeSearch2.ValueComparator vc=new ValueComparator();
            Collections.sort(list, vc);
//            for(Iterator<Map.Entry<String, String>> it=list.iterator();it.hasNext();)
//            {
//                System.out.println(it.next());
//            }
            RandomAccessFile readFile = new RandomAccessFile(file, "rw");
            for (Iterator<Map.Entry<String, String>> it = list.iterator(); it.hasNext(); ) {
                String[] s = it.next().toString().split("=");
                String channelName = s[0];
                String[] CI = s[1].split(",");
                Long start = Long.parseLong(CI[0]);
                Long end = Long.parseLong(CI[1]);
                byte[] data = new byte[(int) (end - start)];
                ByteBuffer buffer = ByteBuffer.allocate(data.length);
                readFile.seek(start);
                readFile.readFully(data);
                buffer = ByteBuffer.wrap(data);
                DoubleBuffer doubleBuffer = buffer.asDoubleBuffer();
                doubleBuffer.position(0);
                double[] doubleData = new double[doubleBuffer.remaining()];
                doubleBuffer.get(doubleData);
                dataMap.get(channelName).addAll(new ArrayList (Arrays.asList(doubleData)));
            }
            readFile.close();
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
                temp = getIndex1(channels, tempTree.getChannelsIndex2());
            }else{
                temp = getIndex2(tempTree.getChannelsIndex2());
            }
            LineNeedToFetch.put(fileAddress, temp);
        }

        return LineNeedToFetch;
    }

    private static Map<String, String>  getIndex1(String[] channels, Map<String, List<Long>> channelMap){
        Map<String, String> channelIndexingMap = new HashMap<>();
        for(String channel: channels){
            List<Long> temp = channelMap.get(channel);
            if(temp == null)
                continue;
            String temp2 = "";
            temp2 += temp.get(0).toString()+",";
            temp2 += temp.get(1).toString();
            channelIndexingMap.put(channel, temp2);
        }

        return channelIndexingMap;
    }

    private static Map<String, String> getIndex2(Map<String, List<Long>> channelMap){     // all channel
        Map<String, String> channelIndexingMap = new HashMap<>();
        Set<String> channels = channelMap.keySet();
        for(String channel: channels){
            List<Long> temp = channelMap.get(channel);
            String temp2 = "";
            temp2 += temp.get(0).toString()+",";
            temp2 += temp.get(1).toString();
            channelIndexingMap.put(channel, temp2);
        }

        return channelIndexingMap;
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
