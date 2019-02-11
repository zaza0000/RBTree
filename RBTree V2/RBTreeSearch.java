package RBTree;

import java.io.*;
import java.util.*;

import javax.json.*;
import javax.json.stream.*;


public class RBTreeSearch {
    public static void main(String[] args) throws IOException{
        if(args.length < 2) {
            System.out.println("lack of parameters");
            return;
        }
        System.out.println("start data+time: "+args[0]);
        System.out.println("end data+time: "+args[1]);
        System.out.println();

        String path = "/Users/jianzhezhang/eclipse-workspace/RedAndBlackTree/Insular-Sz1-CSF";
        Map<String, List<String>> fileData = CSFParser.process(path);

        RedAndBlackTree<String> tree=new RedAndBlackTree<>();
        tree.insertCSF(fileData);
        System.out.println("RBTreeInfo:");
        tree.print();
        tree.checkBlackNum();
        System.out.println();
        List l = tree.search(args[0], args[1]);
        Iterator<RedAndBlackTree.TreeNode> it = l.iterator();
        while(it.hasNext()){
            System.out.println(it.next().toString());
        }
        tree.clear();
    }


}
