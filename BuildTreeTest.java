package RBTree;

public class BuildTreeTest {
    public static void main(String[] args) {
        System.out.println("Red and Black Tree");

        int a[] = {10, 40, 30, 60, 90, 70, 20, 50, 80};
        String time[] ={"21.08.00","21.08.30"};

        RedAndBlackTree<String> tree=new RedAndBlackTree<>();
        System.out.println("insert:");
        for(int i = 0; i < a.length ; i++) {
            System.out.println("num: "+a[i]);
            tree.insert(Integer.toString(a[i]));
            tree.checkBlackNum();
            System.out.println("tree size: "+tree.getSize());
        }

        //System.out.println("in-order: ");
        //tree.inOrder();
        System.out.println();
        System.out.println("RBTreeInfo:");
        tree.print();
        System.out.println();
        System.out.println("remove:");
        for(int i = 0; i < a.length ; i++)
        {
            System.out.println("num: "+a[i]);
            tree.remove(Integer.toString(a[i]));
            tree.checkBlackNum();
            System.out.println("tree size: "+tree.getSize());
        }

        tree.clear(); // destroy the tree

        //System.out.println(tree.getSize());

        //System.out.println(node.toString());
    }
}
