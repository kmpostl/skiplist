import java.lang.*;
import java.util.*;

class Node<AnyType>
{
	private AnyType data;
	private int height;
	public ArrayList<Node<AnyType>> nextNodes;

	Node(int height)
	{
		this.height = height;
		this.data = null;
		nextNodes = new ArrayList<Node<AnyType>>();
		for (int i = 0; i < height; i++)
			nextNodes.add(null);
	}

	Node(AnyType data, int height)
	{
		this.data = data;
		this.height = height;
		nextNodes = new ArrayList<Node<AnyType>>();
		for (int i = 0; i < height; i++)
			nextNodes.add(null);
	}

	// Return data
	public AnyType value()
	{
		return this.data;
	}

	// Return height of node
	public int height()
	{
		return this.height;
	}

	// Get next node
	public Node<AnyType> next(int level)
	{
		if (level < height && level >= 0)
			return nextNodes.get(level);
		return null;
	}

	public void setNext(int level, Node<AnyType> node)
	{
		if (level < height && level >= 0)
			nextNodes.set(level, node);
	}

	public void grow()
	{
		++this.height;
		nextNodes.add(null);
	}

	public void maybeGrow()
	{
		if (Math.random() < 0.5)
			grow();
	}

	// Trim height of node to desired value
	public void trim(int height)
	{
		if (height > 0)
		{
			for (int i = this.height; i > height; i--)
				nextNodes.remove(i);
			this.height = height;
		}
	}
}

public class SkipList<AnyType extends Comparable<AnyType>>
{
	private Node<AnyType> head;
	private int height;
	private int size = 0;
	private static final int defaultHeight = 1;

	SkipList()
	{
		this.height = defaultHeight;
		this.head = new Node<AnyType>(this.height);
	}

	SkipList(int height)
	{
		this.height = ((height < defaultHeight) ? defaultHeight : height);
		this.head = new Node<AnyType>(this.height);
	}

	public int size()
	{
		return size;
	}

	public int height()
	{
		return height;
	}

	public Node<AnyType> head()
	{
		return head;
	}

	// Max height based on logarithm base change rules
	private static int getMaxHeight(int n)
	{
		if (n <= 1)
			return 1;
		double result = Math.ceil((Math.log(n) / Math.log(2)));
		return (int) result;
	}

	// Generate random height for new node
	private static int generateRandomHeight(int maxHeight)
	{
		double rand = Math.random();
		int result = defaultHeight;

		while (rand < 0.5 && result < maxHeight)
		{
			rand = Math.random();
			result += 1;
		}

		return result;
	}

	private void growSkipList()
	{
		Node<AnyType> current = head;
		HashMap<Integer, Node<AnyType>> heightMap = new HashMap<Integer, Node<AnyType>>();

		// Populate hashmap with head for every height possible
		for (int i = defaultHeight; i <= this.height; i++)
		{
			heightMap.put(i, current);
		}

		// Loop through all nodes and coinflip(maybeGrow) to see if they grow
		for (int i = 0; i < size; i++)
		{
			// Step current forwards one since we need to change every node
			current = current.next(0);
			int oldHeight = current.height();

			// Don't want to exceed max height
			if (oldHeight < this.height)
				current.maybeGrow();

			// If the height changed we need to set the last node at that height to point to the changed node
			if (oldHeight < current.height())
			{
				Node<AnyType> oldNode = heightMap.get(oldHeight);
				// Changed node now points ahead to the correct node
				current.setNext(current.height(), oldNode.next(current.height()));
				// Previously tall node now points ahead to the changed node
				oldNode.setNext(current.height(), current);
			}

			// Regardless if current changed its now the latest node at that height
			heightMap.replace(current.height(), current);
		}
	}

	public Node<AnyType> get(AnyType data)
	{
		int level = this.height - defaultHeight;
		boolean found = false;
		Node<AnyType> current = this.head;
		Node<AnyType> next = current.next(level);

		if (next != null)
			while (next.value().compareTo(data) < 0)
				{
					current = next;
					next = next.next(level);
				}

		while (!found)
		{
			next = current.next(level);
			// Check if level is 0 and next is null or greater than data searched. Then its not there
			if (level == 0 && (next == null || next.value().compareTo(data) > 0))
				return null;
			// Next is the node we're searching for
			if (next != null && next.value().compareTo(data) == 0)
				return next;
			else
			{
				// We need to move down since either next is null or greater than search value
				if (next == null || next.value().compareTo(data) > 0)	// Move down
					level--;
				else													// Move right
					current = next;
			}
		}
		return next;
	}

	public boolean contains(AnyType data)
	{
		return (get(data) != null);
	}

	// Basically a random height insert wrapper
	public void insert(AnyType data)
	{
		insert(data, generateRandomHeight(this.height));
	}

	public void insert(AnyType data, int height)
	{
		// Just in case someone passes a node in with a height greater than the head node
		if (height > head.height())
		{
			this.height++;
			head.grow();
		}

		// Insert the new node
		insertWrapped(data, height);

		// Grow skip list if it needs it
		if (getMaxHeight(this.size) > head.height())
		{
			this.height++;
			head.grow();
			growSkipList();
		}
	}

	// Actual insertion method
	private void insertWrapped(AnyType data, int height)
	{
		Node<AnyType> current = this.head;
		ArrayDeque<Node<AnyType>> breadCrumbs = new ArrayDeque<Node<AnyType>>();
		int level = this.height - defaultHeight;
		boolean found = false;
		Node<AnyType> next;
		Node<AnyType> insertMe = new Node<AnyType>(data, height);

		while (!found)
		{
			next = current.next(level);

			// Position is found since level is 0 and next is either null or greater than the new node
			if (level == 0 && (next == null || next.value().compareTo(data) >= 0))
			{
				found = true;
				// Web currents next to new node's next
				insertMe.setNext(level, next);
				// Now fix by setting currents next to new node
				current.setNext(level, insertMe);
				this.size++;
				break;
			}
			// Next is not null and new node is greater than it, so move right
			else if (next != null && next.value().compareTo(data) < 0)
			{
				current = current.next(level);
				continue;
			}
			// (Next is either null or greater than the node) and level is more than 0 so move down
			else
			{
				if (level <= insertMe.height() - 1)
				{
					// Mark a breadcrumb since the new nodes height will be greater than or equal to currents
					breadCrumbs.push(current);
					level--;
					continue;
				}
				else
				{
					level--;
					continue;
				}
			}
		}

		// Fix all the breadcrumbs left along the way, bottom to top
		for (int i = breadCrumbs.size(); i > 0; i--)
		{
			// Spider-web nodes back together
			Node<AnyType> fixMe = breadCrumbs.pop();
			insertMe.setNext(i, fixMe.next(i));
			fixMe.setNext(i, insertMe);
		}
	}

	public void delete(AnyType data)
	{
		deleteWrapped(data);
		if (getMaxHeight(this.size) < head.height())
		{
			this.height--;
			int level = head.height() - 1;
			recursiveTrim(head.next(level), level);
			head.trim(level);
		}
	}

	private void deleteWrapped(AnyType data)
	{
		Node<AnyType> current = this.head;
		ArrayDeque<Node<AnyType>> breadCrumbs = new ArrayDeque<Node<AnyType>>();
		int level = this.height - defaultHeight;
		boolean found = false;
		Node<AnyType> next = current.next(level);

		while (!found)
		{
			next = current.next(level);

			if (level == 0 && next == null)
				return;
			if (level == 0 && next.value().compareTo(data) == 0)	// Found
			{
				// Next is now value
				found = true;
				breadCrumbs.push(current);
			}
			else
			{
				if (next == null || next.value().compareTo(data) >= 0)	// If they're the same or next is greater move down
				{
					// Disregard height since thats solved below
					breadCrumbs.push(current);
					level--;
				}
				else
				{
					current = current.next(level);
				}
			}
		}

		// Breadcrumb fixing and actually removing node by getting rid of references
		// Depending on height of deleted node we pop only the most recent heights as we went down
		for (int i = 0; i < next.height(); i++)
		{
			Node<AnyType> fixMe = breadCrumbs.pop();
			fixMe.setNext(i, next.next(i));
		}
	}

	private void recursiveTrim(Node<AnyType> trimMe, int level)
	{
		if (trimMe == null)
			return;
		recursiveTrim(trimMe.next(level), level);
		trimMe.trim(level);
		return;
	}

	public static double hoursSpent()
	{
		return 0.0;
	}

	public static double difficultyRating()
	{
		return 0.0;
	}


	public static void main(String[] args)
	{
		// SkipList<Integer> s = new SkipList<Integer>();
		//
		// s.insert(10);
		// s.insert(20);
		// s.insert(3);
		// s.insert(15);
		// s.insert(5);

		SkipList<Integer> skiplist = new SkipList<Integer>();

		long totalTime = 0, start, end, total;

		start = System.nanoTime();
		// Insert lots of elements. This will take a while.
		for (int i = 0; i < 1000000; i++)
			skiplist.insert(RNG.getUniqueRandomInteger());
		end = System.nanoTime();
		totalTime = (end - start);
		System.out.println("Total time to insert " + totalTime);

		// Time how long it takes to search for an integer this is in the skip list.
		totalTime = 0;
		int used = RNG.getRandomUsedInteger();
		start = System.nanoTime();
		boolean success = skiplist.contains(used);
		end = System.nanoTime();
		totalTime += (end - start);

		System.out.println("Total time for node that exists " + totalTime + "\t\t Value: " + used + "\t\tSuccess?: "+ success);
		// Time how long it takes to search for an integer this is not in the skip list.
		int unused = RNG.getRandomUnusedInteger();
		start = System.nanoTime();
		success &= !skiplist.contains(unused);
		end = System.nanoTime();
		totalTime += (end - start);
		System.out.println("Total time for node that doesn't exists " + totalTime + "\t\t Value: " + unused + "\t\tSuccess?: "+ success);

		System.out.println((success && totalTime < 500000) ? "Hooray!" : "fail whale :(");
		/*for(int i = 0; i < skippy.size(); i++)
		{
			current = current.next(0);
			//for (int j = 0; j < current.height(); j++) {
				System.out.print(current.value() + "->");
			//}
		}*/

		//System.out.println("Max height of " + s.size() + " is equal to " + getMaxHeight(s.size()));
		//System.out.println("Max height of " + 3 + " is equal to " + getMaxHeight(3));
	}
}
