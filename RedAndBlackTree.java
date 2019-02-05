package RBTree;

public class RedAndBlackTree<T extends Comparable<T>>  {
    private TreeNode<T> Root;    // root - Channel Tree
    private int size;
    private int BlackNodenum;
    private static final boolean RED   = false;
    private static final boolean BLACK = true;

    public class TreeNode<T extends Comparable<T>> {
        // Data part
        private T time;               // key
        private String channel;        // channel name
        private String addressIndex;    // index
        // Tree part
        boolean color;        // red or black
        TreeNode<T> left;    // left child
        TreeNode<T> right;    // right child
        TreeNode<T> parent;    // parent
        TreeNode<Integer> tRoot;     // Time Tree

        public TreeNode(T time, String channel, boolean color, TreeNode<T> parent, TreeNode<T> left, TreeNode<T> right) {
            this.time = time;
            this.channel = channel;
            this.color = color;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }


        public T getKey() {
            return time;
        }

        public String getChannel(){
            return channel;
        }

        public String getAddressIndex(){
            return addressIndex;
        }

        public String toString() {
            return "Channel:"+channel+", time:"+time;
        }
    }

    public RedAndBlackTree() {
        Root=null;
        size = 0;
    }

    public int getSize(){
        return size;
    }

    public void insert(T time){
        TreeNode<T> newNode=new TreeNode<T>(time, null, BLACK,null,null,null);
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
            int temp = cur.getKey().compareTo(node.getKey());
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
        int temp = node.getKey().compareTo(par.getKey());
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

    public void remove(T key) {
        TreeNode<T> node;

        if ((node = search(Root, key)) != null){
            remove(node);
            size--;
        }else{
            System.out.println(key+" Not Found");
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

    private TreeNode<T> search(TreeNode<T> node, T key) {
        while (node!=null) {
            int temp = key.compareTo(node.getKey());

            if (temp < 0)
                node = node.left;
            else if (temp > 0)
                node = node.right;
            else
                return node;
        }

        return node;
    }

    public TreeNode<T> search(T key) {
        return search(Root, key);
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

    private void printRBTreeInfo(TreeNode<T> tree, T key, int index) {

        if(tree != null) {

            if(index==0)
                System.out.printf("%2s(BLK) is root\n", tree.getKey());
            else if(index==1)
                System.out.printf("%2s(%s) is %2s's rChild\n", tree.getKey(), tree.color == RED?"RED":"BLk", key);
            else
                System.out.printf("%2s(%s) is %2s's lChild\n", tree.getKey(), tree.color == RED?"RED":"BLK", key);

            printRBTreeInfo(tree.left, tree.getKey(), -1);
            printRBTreeInfo(tree.right, tree.getKey(),  1);
        }
    }

    public void print() {
        if (Root != null)
            printRBTreeInfo(Root, Root.getKey(), 0);
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