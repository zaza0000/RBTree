package RBTree;

public class RedAndBlackTree<T extends Comparable<T>>  {
    private TreeNode<T> Root;    // root
    private int size;
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
}