// Kurt Postlmayr
// COP 3503C, Fall 2021
// ku222809

import java.lang.*;
import java.util.*;

class Node<AnyType>
{
	private AnyType data;
	private int height;
	public ArrayList<Node<AnyType>> nextNodes;

	// This constructor creates a node without any data (used for head node)
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

	// Set next node if level is valid
	public void setNext(int level, Node<AnyType> node)
	{
		if (level < height && level >= 0)
			nextNodes.set(level, node);
	}

	// Increase height of node and add null to end of list
	public void grow()
	{
		++this.height;
		nextNodes.add(null);
	}

	// 50/50 chance of calling grow above
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
				nextNodes.remove(i - 1);
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

	// Constructor for user to define a height immediately
	SkipList(int height)
	{
		this.height = ((height < defaultHeight) ? defaultHeight : height);
		this.head = new Node<AnyType>(this.height);
	}

	// Returns number of nodes within list (excluding head)
	public int size()
	{
		return this.size;
	}

	// Same as head node's height
	public int height()
	{
		return this.height;
	}

	// Returns head of list
	public Node<AnyType> head()
	{
		return this.head;
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
			result += 1;
			rand = Math.random();
		}

		return result;
	}

	private void growSkipList()
	{
		Node<AnyType> current = this.head.next(this.height - 1);
		Node<AnyType> oldNode = this.head;

		// Loop through all max height nodes and coinflip(maybeGrow) to see if they grow
		while (current != null)
		{
			int oldHeight = current.height();

			// Don't want to exceed max height
			if (oldHeight < head.height())
				current.maybeGrow();

			// If the height changed we need to set the last node at that height to point to the changed node
			if (oldHeight != current.height())
			{
				// Changed node now points ahead to the correct node
				current.setNext(current.height() - 1, oldNode.next(current.height() - 1));
				// Previously tall node now points ahead to the changed node
				oldNode.setNext(current.height() - 1, current);
				// Set the previous tallest node to current since it grew
				oldNode = current;
			}

			// Step current forwards one since we need to change every node
			current = current.next(this.height - 1);
		}
	}

	public Node<AnyType> get(AnyType data)
	{
		int level = this.height - defaultHeight;
		boolean found = false;
		Node<AnyType> current = this.head;
		Node<AnyType> next = current.next(level);

		// Skip ahead immediately for large lists with many nodes at the top level
		while (next != null && next.value().compareTo(data) < 0)
		{
			current = next;
			next = next.next(level);
		}

		// Regular while not found loop. Works down and right to find desired node
		while (!found)
		{
			next = current.next(level);
			// Safety check
			if (level <= 0 && next == null)
				return null;
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

	// Just checks if get() method can find the node
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
		while(height > head.height())
		{
			this.height++;
			head.grow();
		}

		// Insert the new node
		insertWrapped(data, height);

		// Grow skip list if log base 2 of the new size is more than the previous height
		if (getMaxHeight(this.size) > head.height())
		{
			head.grow();
			growSkipList();
			this.height++;
		}
	}

	// Actual insertion method
	private void insertWrapped(AnyType data, int height)
	{
		Node<AnyType> current = this.head;
		Stack<Node<AnyType>> breadCrumbs = new Stack<Node<AnyType>>();
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
				breadCrumbs.push(current);
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
				// Is the current level going to be less equal to the new nodes height? If so:
				if (level <= insertMe.height() - 1)
				{
					// Mark a breadcrumb since the new nodes height will be greater than or equal to currents
					breadCrumbs.push(current);
					level--;
					continue;
				}
				// Otherwise just move down
				else
				{
					level--;
					continue;
				}
			}
		}

		level = 0;

		// Pop breadcrumbs from going down and change their next to the new node
		while (!breadCrumbs.empty())
		{
			Node<AnyType> fixMe = breadCrumbs.pop();
			insertMe.setNext(level, fixMe.next(level));
			fixMe.setNext(level, insertMe);
			level++;
		}
		// Increase size since we just inserted a new node
		this.size++;
	}

	// Public wrapper for delete. Maintains corect height after deletion
	public void delete(AnyType data)
	{
		boolean deleted = deleteWrapped(data);
		int newHeight = getMaxHeight(this.size);

		// If node actually existed and was deleted, then trim the height
		if(deleted)
		{
			while (newHeight < this.height)
			{
				trimAll(newHeight, this.height);
				this.height--;
			}
		}
	}

	// Short method for trimming all nodes at the current max height, which is this.height
	private void trimAll(int newHeight, int oldHeight)
	{
		int level = oldHeight - 1;
		// Keep track of previous and next nodes
		Node<AnyType> current = this.head;
		Node<AnyType> next = current.next(level);

		while (current != null)
		{
			// Trim previous then move forwards
			current.trim(level);
			current = next;
			if (current != null)
				next = current.next(level);
		}

	}

	// Actual delete method
	private boolean deleteWrapped(AnyType data)
	{
		Node<AnyType> current = this.head;
		Stack<Node<AnyType>> breadCrumbs = new Stack<Node<AnyType>>();
		int level = this.height - defaultHeight;
		boolean found = false;
		Node<AnyType> next = current.next(level);

		while (!found)
		{
			next = current.next(level);

			// Safety check
			if (level <= 0 && next == null)
				return false;
			if (level == 0 && next.value().compareTo(data) == 0)	// Found
			{
				// Next is now value
				found = true;
				breadCrumbs.push(current);
				break;
			}
			else
			{
				// If they're the same or next is greater move down
				if (next == null || next.value().compareTo(data) >= 0)
				{
					// Disregard height since thats solved below
					breadCrumbs.push(current);
					level--;
				}
				else	// Move right
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

		// We deleted the node since we didn't return above, so reduce size
		this.size--;

		return found;
	}

	public static double hoursSpent()
	{
		return 30.0;
	}

	public static double difficultyRating()
	{
		return 5.0;
	}
}
