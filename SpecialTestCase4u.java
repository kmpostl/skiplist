// Sean Szumlanski
// COP 3503, Fall 2021

// ======================
// SpecialTestCase4u.java
// ======================
// Congratulations on finding (and reading) this test case. This one is not run automatically by
// the test-all.sh script and should be run manually instead.
//
// This supplementary test case checks whether the get() method is returning the correct node when
// there are multiple instances of some value in the skip list.
//
// This test case builds the following skip list. It's up to you to ensure your insert() method
// is constructing this skip list correctly before evaluating the results of the get() method.
//
//
//   +---+                                            +---+
//   |   |------------------------------------------->|   |----------------------> null
//   +---+                +---+  +---+                +---+ 
//   |   |--------------->|   |->|   |--------------->|   |----------------------> null
//   +---+                +---+  +---+         +---+  +---+         +---+
//   |   |--------------->|   |->|   |-------->|   |->|   |-------->|   |--------> null
//   +---+         +---+  +---+  +---+         +---+  +---+  +---+  +---+
//   |   |-------->|   |->|   |->|   |-------->|   |->|   |->|   |->|   |--------> null
//   +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+
//   |   |->| 1 |->| 1 |->| 1 |->| 1 |->| 1 |->| 4 |->| 4 |->| 4 |->| 9 |->| 9 |-> null
//   +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+  +---+
//   ^head                 (a)                         (b)           (c)


public class SpecialTestCase4u
{
	public static void main(String [] args)
	{
		int data;
		int height;

		SkipList<Integer> slushie = new SkipList<>(5);

		// Recall that we insert a duplicate value before all other instances of that value, so
		// these duplicates are effectively inserted in reverse order.
		//
		// Notice, by the way, how the use of "data =" and "height =" are totally unnecessary,
		// but they help the reader immediately discern the meaning of each of these arguments.
		// This is just a readability trick I wanted to share with you.

		slushie.insert(data = 1, height = 1);
		slushie.insert(data = 1, height = 4);
		slushie.insert(data = 1, height = 4);
		slushie.insert(data = 1, height = 2);
		slushie.insert(data = 1, height = 1);

		slushie.insert(data = 4, height = 2);
		slushie.insert(data = 4, height = 5);
		slushie.insert(data = 4, height = 3);

		slushie.insert(data = 9, height = 1);
		slushie.insert(data = 9, height = 3);

		// Fetch references to nodes (a), (b), and (c) in the diagram above.

		Node<Integer> a = slushie.head().next(0).next(0).next(0);
		Node<Integer> b = a.next(0).next(0).next(0).next(0);
		Node<Integer> c = b.next(0).next(0);

		// Check whether the get() method is fetching the correct node.

		System.out.print("Node (a) (value = " + a.value() + "): ");
		System.out.println((slushie.get(1) == a) ? "SUCCESS!" : "failwhale :(");

		System.out.print("Node (b) (value = " + b.value() + "): ");
		System.out.println((slushie.get(4) == b) ? "SUCCESS!" : "failwhale :(");

		System.out.print("Node (c) (value = " + c.value() + "): ");
		System.out.println((slushie.get(9) == c) ? "SUCCESS!" : "failwhale :(");
	}
}
