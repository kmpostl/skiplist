// Amelia Castilla
// COP 3503, Fall 2021

// =========================
// SkipList: TestCaseSuperEdgeCase.java
// =========================
// This test case makes sure insertions of height ceil(log2(n + 1)) when 
// inserting at a size jump point function correctly

public class TestCaseSuperEdgeCase {
    public static void main(String[] args) {
        boolean success = true;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            SkipList<Integer> s = new SkipList<Integer>(2);

            s.insert(10, 2);
            s.insert(20, 2);
            s.insert(30, 2);
            s.insert(40, 2);

            // At the fifth insertion, size should increase to 3, which means we
            // can legally insert at height 3 according to footnote 1 of pdf
            int target = (int) (Math.random() * 50.0); // 0-49
            s.insert(target, 3);

            // The newly inserted node should still link up to all other nodes
            // that passed the 50/50 check
            Node<Integer> curNode = s.head().next(s.height() - 1);
            boolean curTest = false;
            int cntNumMax = 0;
            while (curNode != null) {
                cntNumMax++;

                if (curNode.value() == target)
                    curTest = true;

                curNode = curNode.next(s.height() - 1);
            }

            // Safely count the real number of nodes with the max height, along
            // the bottom
            int realNumMax = 0;
            int targetHeight = 3;
            curNode = s.head().next(0);
            while (curNode != null) {
                if (curNode.height() == targetHeight)
                    realNumMax++;

                curNode = curNode.next(0);
            }

            // Ensure that no nodes of max height were unlinked
            if (cntNumMax != realNumMax) {
                success = false;
                System.out.println("You forgot max height nodes!!! loser!!!");
                break;
            }
            // Ensure target is found
            if (!curTest) {
                success = false;
                System.out.println("The target wasn't found!! You suck!!!");
                break;
            }
        }

        System.out.println(success ? "Hooray!" : "fail whale :(");
    }
}
