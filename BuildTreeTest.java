package RBTree;

public class BuildTreeTest {
    public static void main(String[] args) {
        System.out.println("Red and Black Tree");

        int a[] = {10, 40, 30, 60, 90, 70, 20, 50, 80};

        RedAndBlackTree<Integer> tree=new RedAndBlackTree<Integer>();

        for(int i = 0; i < a.length; i++) {
            tree.insert(a[i]);
        }

        System.out.println("in-order: ");
        tree.inOrder();

        //System.out.println(tree.getSize());

        //System.out.println(node.toString());
    }
}
