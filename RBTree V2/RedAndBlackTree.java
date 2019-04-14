package RBTree;

import java.util.*;

public class RedAndBlackTree<T extends Comparable<T>>  {
    private TreeNode<T> Root;
    private int size;
    private int BlackNodenum;
    private static final boolean RED   = false;
    private static final boolean BLACK = true;
    private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public class TreeNode<T extends Comparable<T>> {
        // Data part
        private String startTime;               // key
        private String endTime;
        private String sDate;
        private String sTime;
        private String eDate;
        private String eTime;
        public String fra2_sDate;
        public String fra2_sTime;
        private Map<String, List<Integer>>  channelsIndex;        // channel Index
        private Map<String, List<Long>>  channelsIndex2;        // channel Index
        private String channelName;
        private String fileName;    // indexing
        // Tree part
        boolean color;        // red or black
        TreeNode<T> left;    // left child
        TreeNode<T> right;    // right child
        TreeNode<T> parent;    // parent

        public TreeNode(String fileName, String startTime, String endTime, Map<String, List<Integer>> channelsIndex, String channelName, boolean color, TreeNode<T> parent, TreeNode<T> left, TreeNode<T> right) {
            this.startTime = startTime;
            this.endTime = endTime;
            modifyTime();
            this.fileName = fileName;
            this.channelsIndex = channelsIndex;
            this.channelName = channelName;
            this.color = color;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        public TreeNode(String fileName, String startTime, String endTime, Map<String, List<Long>> channelsIndex, boolean color, TreeNode<T> parent, TreeNode<T> left, TreeNode<T> right) {
            this.startTime = startTime;
            this.endTime = endTime;
            modifyTime();
            this.fileName = fileName;
            this.channelsIndex2 = channelsIndex;
            this.color = color;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        private void modifyTime(){
            String temp1 = this.startTime.toString();
            String temp2 = this.endTime.toString();
            if(!temp1.contains(",") || !temp2.contains(",")) {
                System.out.println("modifyTime error");
                return;
            }
            String[] start = temp1.split(",");
            String[] end = temp2.split(",");
            this.sDate = start[0];
            this.sTime = start[1];
            this.eDate = end[0];
            this.fra2_sDate = end[0];
            this.eTime = end[1];
            this.fra2_sTime = end[1];
            addseconds(end[2]);
        }

        private void addseconds(String sec){
            String[] temp1 = eDate.split("\\.|:");
            String[] temp2 = eTime.split("\\.|:");
            int day = Integer.parseInt(temp1[0], 10),
                    month = Integer.parseInt(temp1[1], 10),
                    year = Integer.parseInt(temp1[2], 10),
                    hour = Integer.parseInt(temp2[0], 10),
                    min = Integer.parseInt(temp2[1], 10),
                    seco = Integer.parseInt(temp2[2], 10);
            double addSec = Double.parseDouble(sec);
            seco += ((int) Math.floor(addSec));
            if(seco >= 60){
                min += (seco / 60);
                seco %= 60;
            }
            if(min >= 60){
                hour += (min / 60);
                min %= 60;
            }
            if(hour >= 24){
                day += (hour / 24);
                hour %= 24;
            }
            int mon = DAYS_IN_MONTH[month - 1]
                    + ((month==2 && (year%4==0 && (year%100!=0 || year%400==0))) ? 1 : 0);
            while(day > mon){
                day -= mon;
                month++;
                if(month > 12){
                    month = 1;
                    year++;
                }
                mon = DAYS_IN_MONTH[month - 1]
                        + ((month==2 && (year%4==0 && (year%100!=0 || year%400==0))) ? 1 : 0);
            }
            this.eDate = (day < 10 ? "0" : "") + day
                    + "." + (month < 10 ? "0" : "") + month
                    + "." + (year < 10 ? "0" : "") + year;
            this.eTime = (hour < 10 ? "0" : "") + hour
                    + "." + (min < 10 ? "0" : "") + min
                    + "." + (seco < 10 ? "0" : "") + seco;
        }

        public String getStartTime(){
            return sTime;
        }

        public String getStartDate() {
            return sDate;
        }

        public String getEndTime(){
            return eTime;
        }

        public String getEndDat(){
            return eDate;
        }

        public String getChannel(){
            return channelName;
        }

        public String getFileName(){
            return fileName;
        }

        public Map<String, List<Integer>> getChannelsIndex(){
            return channelsIndex;
        }

        public Map<String, List<Long>> getChannelsIndex2(){
            return channelsIndex2;
        }

        public String toString() {
            return "filename: "+fileName+", starttime: "+sDate+sTime+", endtime: "+eDate+eTime;
        }

        public int compareTo2(String s1, String s2){ // s1>s2 -> 1
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

        public int compareTo(TreeNode o) {
            String s1 = this.sDate+","+this.sTime;
            String s2 = o.getStartDate()+","+o.getStartTime();
            int s = compareTo2(s1, s2);
            return s;
        }

        // 0- "end" is less than this.start
        // 1- "start" is greater than this.end
        // 2- left part overlapped
        // 3- right part overlapped
        // 4- covered
        // 5- inside (equal)
        public int compareTo(String start, String end) {
            String s1 = this.sDate+","+this.sTime;
            String s2 = this.eDate+","+this.eTime;
            if(compareTo2(s1,end) >= 0)
                return 0;
            if(compareTo2(s2,start) <=0)
                return 1;
            int s = compareTo2(s1, start);
            int e = compareTo2(s2, end);
            if(s>0 && e>=0)
                return 2;
            else if(s>0 && e<0)
                return 4;
            else if(s<=0 && e>=0)
                return 5;
            else
                return 3;
        }
    }

    public RedAndBlackTree() {
        Root=null;
        size = 0;
    }

    public int getSize(){
        return size;
    }

    private void printIndexList(Map<String, List<Integer>> list){
        Set<String> keys = list.keySet();
        for(String key: keys){
            List<Integer> temp = list.get(key);
            System.out.println(key);
            System.out.print(temp.get(0)+" "+temp.get(1));
            System.out.println();
        }
    }

    public void insertCSF(Map<String, List<String>> HeaderData, Map<String, Map<String, List<Integer>>> ChannelIndex){
        Set<String> keys = HeaderData.keySet();
        for(String key:keys){
            List<String> headerData = HeaderData.get(key);
            //printIndexList(ChannelIndex.get(key));
            String starttime = "";
            String endtime = "";
            Iterator<String> it = headerData.iterator();
            if(it.hasNext())
                starttime = it.next();
            if(!it.hasNext())
                endtime = starttime;
            while(it.hasNext()){
                endtime = it.next();
            }
            //System.out.println(key);
            //System.out.println(starttime);
            //System.out.println(endtime);
            TreeNode<T> newNode=new TreeNode<T>(key, starttime, endtime, ChannelIndex.get(key),null, BLACK,null,null,null);
            if(newNode == null){
                System.out.println("Failed to create a new node");
                return;
            }
            boolean success = insertNode(newNode);
            if(success == false)
                System.out.println("Failed to insert "+key);
            else
                size++;
        }
    }

    public void insertCSF2(Map<String, List<String>> HeaderData, Map<String, Map<String, List<Long>>> ChannelIndex){
        Set<String> keys = HeaderData.keySet();
        for(String key:keys){
            List<String> headerData = HeaderData.get(key);
            String starttime = "";
            String endtime = "";
            Iterator<String> it = headerData.iterator();
            if(it.hasNext())
                starttime = it.next();
            if(!it.hasNext())
                endtime = starttime;
            while(it.hasNext()){
                endtime = it.next();
            }
            //System.out.println(key);
            //System.out.println(starttime);
            //System.out.println(endtime);
            TreeNode<T> newNode=new TreeNode<T>(key, starttime, endtime, ChannelIndex.get(key), BLACK,null,null,null);
            if(newNode == null){
                System.out.println("Failed to create a new node");
                return;
            }
            boolean success = insertNode(newNode);
            //System.out.println(newNode.toString());
            if(success == false)
                System.out.println("Failed to insert "+key);
            else
                size++;
        }
    }

    public void insert(T time){
        TreeNode<T> newNode=new TreeNode<T>(null, time.toString(),null, null,null, BLACK,null,null,null);
        if(newNode == null){
            System.out.println("Failed to create a new node");
            return;
        }
        boolean success = insertNode(newNode);
        if(success == false)
            System.out.println("Failed to insert "+time);
        else
            size++;
    }

    private boolean insertNode(TreeNode<T> node){
        if(this.Root == null) {
            this.Root = node;
            return true;
        }

        TreeNode<T> cur = this.Root;
        TreeNode<T> par = null;
        while(cur != null){
            par = cur;
            int temp = cur.compareTo(node);
            if(temp > 0)        // cur.key > newNode.key
                cur = cur.left;
            else if(temp < 0)       // cur.key > newNode.key
                cur = cur.right;
            else{
                System.out.println(node.toString()+" is already exist");
                return false;
            }
        }

        node.parent = par;
        int temp = node.compareTo(par);
        if (temp < 0)
            par.left = node;
        else
            par.right = node;

        node.color = RED;         // set color

        insertFixUp(node);

        return true;
    }

    private void insertFixUp(TreeNode<T> node) {
        TreeNode<T> parent;
        while ((node.parent !=null) && (node.parent.color == RED)) {
            parent = node.parent;
            TreeNode<T> gparent = parent.parent;
            if (parent == gparent.left) {
                // case 1
                TreeNode<T> uncle = gparent.right;
                if((uncle!=null) && (uncle.color==RED)){
                    uncle.color = BLACK;
                    parent.color = BLACK;
                    gparent.color = RED;
                    node = gparent;
                    parent = node.parent;
                    continue;
                }
                // case 2
                if(parent.right==node){
                    node = parent;
                    leftRotate(node);
                    parent = node.parent;
                }
                // case 3
                if(parent.left==node) {
                    parent.color = BLACK;
                    gparent.color = RED;
                    rightRotate(gparent);
                }
            }else {
                // case 1
                TreeNode<T> uncle = gparent.left;
                if((uncle!=null) && (uncle.color==RED)){
                    uncle.color = BLACK;
                    parent.color = BLACK;
                    gparent.color = RED;
                    node = gparent;
                    parent = node.parent;
                    continue;
                }
                // case 2
                if(parent.left==node){
                    node = parent;
                    rightRotate(node);
                    parent = node.parent;
                }
                // case 3
                if(parent.right==node) {
                    parent.color = BLACK;
                    gparent.color = RED;
                    leftRotate(gparent);
                }

            }
        }
        Root.color = BLACK;
    }

    private void leftRotate(TreeNode<T> node) {
        TreeNode<T> right = node.right;
        node.right = right.left;
        if(right.left != null)
            right.left.parent = node;
        right.parent = node.parent;
        if(node.parent == null){
            this.Root = right;
        }else{
            if(node.parent.left == node)
                node.parent.left = right;
            else
                node.parent.right = right;
        }
        right.left = node;
        node.parent = right;
    }

    private void rightRotate(TreeNode<T> node){
        TreeNode<T> left = node.left;
        node.left = left.right;
        if(left.right != null)
            left.right.parent = node;
        left.parent = node.parent;
        if(node.parent == null){
            this.Root = left;
        }else{
            if(node.parent.left == node)
                node.parent.left = left;
            else
                node.parent.right = left;
        }
        left.right = node;
        node.parent = left;
    }

    private void remove(String start, String end) {
        TreeNode<T> node;

        if ((node = search(Root, start, end)) != null){
            remove(node);
            size--;
        }else{
            System.out.println("File Not Found");
        }
    }

    private void remove(TreeNode<T> node){
        TreeNode<T> nChild, nParent; // (right) child of node, parent of node
        boolean color;

        if(node.left!=null && node.right!=null){
            TreeNode<T> replace = node;
            replace = successor(replace);

            if(node.parent!=null){
                if (node.parent.left == node)
                    node.parent.left = replace;
                else
                    node.parent.right = replace;
            }else{
                this.Root = replace;
            }
            nChild = replace.right;
            nParent = replace.parent;
            color = replace.color;
            if(nParent == node){
                nParent = replace;
            }else{
                if (nChild != null)
                    nChild.parent = nParent;
                nParent.left = nChild;
                replace.right = node.right;
                node.right.parent = replace;
            }

            replace.parent = node.parent;
            replace.color = node.color;
            replace.left = node.left;
            node.left.parent = replace;
            if(color == BLACK)
                removeFixUp(nChild, nParent);
            node = null;
            return ;
        }

        if(node.left !=null){
            nChild = node.left;
        }else{
            nChild = node.right;
        }
        nParent = node.parent;
        color = node.color;

        if(nChild != null)
            nChild.parent = nParent;

        if(nParent!=null){
            if(nParent.left == node)
                nParent.left = nChild;
            else
                nParent.right = nChild;
        }else{
            this.Root = nChild;
        }

        if(color == BLACK)
            removeFixUp(nChild, nParent);
        node = null;
    }

    private void removeFixUp(TreeNode<T> node, TreeNode<T> parent) {
        TreeNode<T> nBrother; // brother of node

        while((node==null || node.color==BLACK) && (node != this.Root)){
            if(parent.left == node){
                nBrother = parent.right;
                if(nBrother.color == RED){
                    // Case 1:
                    nBrother.color = BLACK;
                    parent.color = RED;
                    leftRotate(parent);
                    nBrother = parent.right;
                }
                if((nBrother.left==null || nBrother.left.color==BLACK) &&
                        (nBrother.right==null || nBrother.right.color==BLACK)){
                    // Case 2:
                    nBrother.color = RED;
                    node = parent;
                    parent = node.parent;
                }else{
                    if(nBrother.right==null || nBrother.right.color==BLACK){
                        // Case 3:
                        nBrother.left.color = BLACK;
                        nBrother.color = RED;
                        rightRotate(nBrother);
                        nBrother = parent.right;
                    }
                    // Case 4:
                    nBrother.color = parent.color;
                    parent.color = BLACK;
                    nBrother.right.color = BLACK;
                    leftRotate(parent);
                    node = this.Root;
                    break;
                }
            }else{
                nBrother = parent.left;
                if(nBrother.color == RED){
                    // Case 1:
                    nBrother.color = BLACK;
                    parent.color = RED;
                    rightRotate(parent);
                    nBrother = parent.left;
                }
                if((nBrother.left==null || nBrother.left.color==BLACK) &&
                        (nBrother.right==null || nBrother.right.color==BLACK)){
                    // Case 2:
                    nBrother.color = RED;
                    node = parent;
                    parent = node.parent;
                }else{
                    if(nBrother.left==null || nBrother.left.color==BLACK){
                        // Case 3:
                        nBrother.right.color = BLACK;
                        nBrother.color = RED;
                        leftRotate(nBrother);
                        nBrother = parent.left;
                    }
                    // Case 4:
                    nBrother.color = parent.color;
                    parent.color = BLACK;
                    nBrother.left.color = BLACK;
                    rightRotate(parent);
                    node = this.Root;
                    break;
                }
            }
        }
        if(node!=null)
            node.color = BLACK;
    }

    public TreeNode<T> successor(TreeNode<T> node) {
        if (node.right != null)
            return minimum(node.right);

        TreeNode<T> succ = node.parent;
        while ((succ!=null) && (node==succ.right)) {
            node = succ;
            succ = succ.parent;
        }

        return succ;
    }

    private TreeNode<T> minimum(TreeNode<T> node) {
        if (node == null)
            return null;
        while(node.left != null)
            node = node.left;
        return node;
    }

    public TreeNode<T> predecessor(TreeNode<T> node) {
        if (node.left != null)
            return maximum(node.left);

        TreeNode<T> pre = node.parent;
        while ((pre!=null) && (node==pre.left)) {
            node = pre;
            pre = pre.parent;
        }

        return pre;
    }

    private TreeNode<T> maximum(TreeNode<T> node) {
        if (node == null)
            return null;
        while(node.right != null)
            node = node.right;
        return node;
    }

    private TreeNode<T> search(TreeNode<T> node, String s, String e) {
        while (node!=null) {
            int temp = node.compareTo(s, e);

            if (temp == 0)
                node = node.left;
            else if (temp == 1)
                node = node.right;
            else
                return node;
        }

        return node;
    }

    private TreeNode<T> searchNode(String s, String e) {
        return search(Root, s, e);
    }

    public List<TreeNode<T>> search(String s, String e) {
        List<TreeNode<T>> treeNodeList = new ArrayList<>();
        dfs(Root, s, e, treeNodeList);
        return treeNodeList;
    }

    private void dfs(TreeNode<T> node, String s, String e, List<TreeNode<T>> nodelist){
        if(node == null)
            return;
        int temp = node.compareTo(s, e);
        if(temp<=5 && temp >=2){
            nodelist.add(node);
            if(temp == 2)
                dfs(node.left, s, e, nodelist);
            else if(temp == 3)
                dfs(node.right, s, e, nodelist);
            else if(temp == 4) {
                dfs(node.left, s, e, nodelist);
                dfs(node.right, s, e, nodelist);
            }else{
                return;
            }
        }else if(temp == 0){
            dfs(node.left, s, e, nodelist);
        }else if(temp == 1){
            dfs(node.right, s, e, nodelist);
        }
    }

    private void preOrder(TreeNode<T> tree) {
        if(tree != null) {
            System.out.print(tree.toString()+" ");
            preOrder(tree.left);
            preOrder(tree.right);
        }
    }

    public void preOrder() {
        preOrder(Root);
    }

    private void inOrder(TreeNode<T> tree) {
        if(tree != null) {
            inOrder(tree.left);
            System.out.print(tree.toString()+" ");
            inOrder(tree.right);
        }
    }

    public void inOrder() {
        inOrder(Root);
    }



    private void postOrder(TreeNode<T> tree) {
        if(tree != null)
        {
            postOrder(tree.left);
            postOrder(tree.right);
            System.out.print(tree.toString()+" ");
        }
    }

    public void postOrder() {
        postOrder(Root);
    }

    private void printRBTreeInfo(TreeNode<T> tree, String key, int index) {

        if(tree != null) {

            if(index==0)
                System.out.printf("%2s(BLK) is root\n", tree.getFileName());
            else if(index==1)
                System.out.printf("%2s(%s) is %2s's rChild\n", tree.getFileName(), tree.color == RED?"RED":"BLk", key);
            else
                System.out.printf("%2s(%s) is %2s's lChild\n", tree.getFileName(), tree.color == RED?"RED":"BLK", key);

            printRBTreeInfo(tree.left, tree.getFileName(), -1);
            printRBTreeInfo(tree.right, tree.getFileName(),  1);
        }
    }

    public void print() {
        if (Root != null)
            printRBTreeInfo(Root, Root.getFileName(), 0);
    }

    private void checkBlackNum(TreeNode<T> node, int count){
        if(node == null){
            count++;
            //System.out.print(count+" ");
            if(BlackNodenum == -1)
                BlackNodenum = count;
            else if(BlackNodenum > -1){
                if(BlackNodenum != count)
                    BlackNodenum = -2;
            }
            return;
        }
        if(node.color == BLACK){
            count++;
        }
        if(node.color == RED && node.parent.color == RED)
            BlackNodenum = -3;
        checkBlackNum(node.left, count);
        checkBlackNum(node.right, count);
    }


    public void checkBlackNum(){
        BlackNodenum = -1;
        checkBlackNum(Root, 0);
        if(BlackNodenum == -3)
            System.out.println("Not a valid RBTree, two successive red nodes");
        if(BlackNodenum == -2)
            System.out.println("Not a valid RBTree, different number of black nodes");
        else if(BlackNodenum == 0)
            System.out.println("Empty Tree");
        else
            System.out.println("Number of Black Nodes in each path: "+BlackNodenum);
    }

    private void destroy(TreeNode<T> tree) {
        if (tree==null)
            return ;

        if (tree.left != null)
            destroy(tree.left);
        if (tree.right != null)
            destroy(tree.right);

        tree=null;
    }

    public void clear() {
        destroy(Root);
        Root = null;
    }

}