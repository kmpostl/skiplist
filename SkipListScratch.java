///////////////////////////////////////////////////////////////////////////////////////////TestCase08 main removed///////////////////////////////////////////////////////////////////////////////////////////
ArrayList<Integer> heights = new ArrayList<Integer>();
SkipList<Integer> skiplist = new SkipList<Integer>();

skiplist.insert(RNG.getUniqueRandomInteger());
TestCase08.checkList(skiplist, size = 1, height = 1);

skiplist.insert(RNG.getUniqueRandomInteger());
TestCase08.checkList(skiplist, size = 2, height = 1);

skiplist.insert(RNG.getUniqueRandomInteger());
TestCase08.checkList(skiplist, size = 3, height = 2);

skiplist.insert(RNG.getUniqueRandomInteger());
TestCase08.checkList(skiplist, size = 4, height = 2);

skiplist.insert(r = RNG.getUniqueRandomInteger());
TestCase08.checkList(skiplist, size = 5, height = 3);

// Loop through skip list and get the heights of all the nodes.
Node<Integer> temp = skiplist.head().next(0);
for (int i = 0; i < 5; i++)
{
	if (temp.value() != r)
		heights.add(temp.height());
	System.out.println("value: " + temp.value() + "\theight: " + temp.height());
	temp = temp.next(0);
}

// Delete element. Check that things are okay.
skiplist.delete(r);
TestCase08.checkList(skiplist, size = 4, height = 2);

// Loop through the list to ensure that all the tallest nodes got
// truncated.
boolean success = true;
temp = skiplist.head().next(0);
for (int i = 0; i < 4; i++)
{
	success &= (temp.height() == Math.min(heights.get(i), 2));
	System.out.println("i value == " + i + "\tAnd success == " + success + "\tAnd height = " + temp.height() + "\theights array is " + heights.get(i));
	temp = temp.next(0);
}

System.out.println(success ? "Hooray!" : "fail whale :(");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////











///////////////////////////////////////////////////////////////////////////////////////////Recursive Get///////////////////////////////////////////////////////////////////////////////////////////
public Node<AnyType> get(AnyType data)
{
	Node<AnyType> current = head;
	return getWrapped(current, data);
}

// THIS SHOULD BE ITERATIVE, NO REASON TO BE RECURSIVE
// Recursively return a node within the list, unless its not there then null
private Node<AnyType> getWrapped(Node<AnyType> current, AnyType data)
{
	boolean found = false;
	Node<AnyType> current = head;
	Node<AnyType> next = new current.next(level);
	int level = head.height();
	while (!found)
	{
		if (level == 0 && (next == null || next.value().compareTo(data) > 0))
			return null;
		if (next.value().compareTo(data) == 0)
			found = true;
		else
		{
			if (next.value().compareTo(data) < 0)
			{
				current = current.next(level);
				next = current.next(level);
			}
			else
			{
				level -= 1;
				next = current.next(level);
			}
		}
	}
	return next;
	/*AnyType currentData = current.value();
	int level = current.height() - 1;

	if (level < 0)
		return null;

	Node<AnyType> next = current.next(level);

	if (next == null || next.value().compareTo(data) > 0) // decrease level
		return getWrapped(current.next(level - 1), data);

	if (next.value().compareTo(data) < 0 || level == 0) // maintain level
		return getWrapped(next, data);

	return next;*/
}



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////Insertion wrapped recursive///////////////////////////////////////////////////////////////////////////////////////////
private Node<AnyType> insertWrapped(Node<AnyType> current, AnyType data, int level, int height)
{
	Node<AnyType> next = current.next(level);
	if (next == null && level == 0)					// End of list
	{
		Node<AnyType> insertMe = new Node<AnyType>(data, height);
		current.setNext(level, insertMe);
		this.size++;
		return insertMe;
	}
	if (next != null && next.value().compareTo(data) < 0)	// Go right
		return insertWrapped(next, data, level, height);
	else											// Move down
	{
		// Level is 0 and right was checked, therefore this is the position
		if (level == 0)
		{
			Node<AnyType> insertMe = new Node<AnyType>(data, height);
			insertMe.setNext(level, next);
			current.setNext(level, insertMe);
			this.size++;
			return insertMe;
		}
		else if (level < (height - 1))
		{
			Node<AnyType> replace = insertWrapped(current.nextNodes.get(level - 1), data, level - 1, height);			// LINE TOO LONG
			replace.setNext(level, next);
			current.setNext(level, replace);
			return replace;
		}
	}
	return null;
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////Newest Insertion Recursive///////////////////////////////////////////////////////////////////////////////////////////
private Node<AnyType> insertWrapped(Node<AnyType> current, AnyType data, int level, int height)
{
	Node<AnyType> next = current.next(level);
	if (next == null && level == 0)					// End of list
	{
		Node<AnyType> insertMe = new Node<AnyType>(data, height);
		current.setNext(level, insertMe);
		this.size++;
		return insertMe;
	}
	if (next != null && next.value().compareTo(data) < 0)	// Go right
		return insertWrapped(next, data, level, height);
	else											// Move down
	{
		// Level is 0 and right was checked, therefore this is the position
		if (level == 0)
		{
			Node<AnyType> insertMe = new Node<AnyType>(data, height);
			insertMe.setNext(level, next);
			current.setNext(level, insertMe);
			this.size++;
			return insertMe;
		}
		else
		{
			Node<AnyType> replace = insertWrapped(current, data, level - 1, height);
			replace.setNext(level, next);
			current.setNext(level, replace);
			return replace;
		}
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class SkipList<AnyType extends Comparable<AnyType>>
{
	private Node<AnyType> head;
	private int height;
	private int size = 0;
	private static final int deafultHeight = 1;							//////////////////////////////////////////////// May need to change to 1
	private ArrayList<AnyType> breadCrumbs;

	SkipList()
	{
		this.height = deafultHeight;
		this.head = new Node<AnyType>(this.height);
	}

	SkipList(int height)
	{
		this.height = ((height < deafultHeight) ? deafultHeight : height);
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
		/*if (this.size() == 0)
			return 1;*/
		return (int) (Math.log(n) / Math.log(2));
	}

	// Generate random height for new node
	private static int generateRandomHeight(int maxHeight)
	{
		double rand = Math.random();
		int result = deafultHeight;

		while (rand < 0.5 && result < maxHeight)
		{
			rand = Math.random();
			result += 1;
		}

		return result;
	}

	public Node<AnyType> get(AnyType data)
	{
		int level = this.height - 1;
		boolean found = false;
		Node<AnyType> current = head;
		Node<AnyType> next = current.next(level);

		while (!found)
		{
			if (level == 0 && (next == null || next.value().compareTo(data) > 0))
				return null;
			if (next.value().compareTo(data) == 0)
				found = true;
			else
			{
				if (next.value().compareTo(data) < 0)
				{
					current = current.next(level);
					next = current.next(level);
				}
				else
				{
					level -= 1;
					next = current.next(level);
				}
			}
		}
		return next;
	}

	public boolean contains(AnyType data)
	{
		return (get(data) == null ? false : true);
	}

	// Basically a random height insert wrapper
	public void insert(AnyType data)
	{
		insert(data, generateRandomHeight(this.height));
	}

	public void insert(AnyType data, int height)
	{
		/*Node<AnyType> current = head;
		Node<AnyType> next = current.next();
		Node<AnyType> newNode = null;
		//boolean positionFound = false;
		int level = head.height();

		if (next == null)
		{
			newNode = new Node<AnyType>(data, deafultHeight);
			head.setNext(deafultHeight, newNode);
		}

		while (newNode == null)
		{
			if ((next == null && level == 0) || next.value().compareTo(data) == 0)
			{
				newNode = new Node<AnyType>(data, deafultHeight);
				next.setNext(newNode);
			}


			breadCrumbs.add(next);
		}

		int level = head.height() - 1;
		if (level < 0)
			level = 0;

		Node<AnyType> current = head;*/
		//System.out.println(this.height);
		insertWrapped(head, data, this.height - 1, height);
	}

	//wait this is perfect for recursion. You can get both inserting and breadcrumb pointers set on step up

	public Node<AnyType> insertWrapped(Node<AnyType> current, AnyType data, int level, int height)
	{
		Node<AnyType> next = current.next(level);
		if (next == null && level == 0)					// End of list
		{
			Node<AnyType> insertMe = new Node<AnyType>(data, height);
			current.setNext(level, insertMe);
		}
		if (next != null && next.value().compareTo(data) < 0)	// Go right
			return insertWrapped(next, data, level, height);
		else											// Move down
		{
			// Level is 0 and right was checked, therefore this is the position
			if (level == 0)
			{
				Node<AnyType> insertMe = new Node<AnyType>(data, height);
				insertMe.setNext(level, next);
				current.setNext(level, insertMe);
				return insertMe;
			}
			else if (level < (height - 1))
			{
				Node<AnyType> replace = insertWrapped(current.nextNodes.get(level - 1), data, level - 1, height);			// LINE TOO LONG
				replace.setNext(level, next);
				current.setNext(level, replace);
				return replace;
			}
		}
		return null;
	}

	/*private boolean containsWrapped(Node<AnyType> current, AnyType data);
	{
		AnyType currentData = current.value();
		int level = current.height() - 1;

		if(level < 0)
			return false;

		Node<AnyType> next = current.nextNodes.get(level);

		if (next == null || next.value().compareTo(data) > 0) // next > data
			return containsWrapped(current.nextNodes.get(level - 1), data);
		if (next.value().compareTo(data) < 0) // next < data
			return containsWrapped(next, data);

		return true;
	}*/

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
		SkipList<Integer> skippy = new SkipList<Integer>();
		skippy.insert(3);
		skippy.insert(4);
		skippy.insert(5);
		Node<Integer> test = skippy.get(4);
		System.out.println(test.height());
		System.out.println(test.value());
	}
}
