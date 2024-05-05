import java.lang.*;
import java.util.*;

public class NodeTest<AnyType>
{
	AnyType data;
	int height;
	//ArrayList or array for next nodes????
	ArrayList<NodeTest<AnyType>> nextNodes; // initialized to null

	NodeTest()
	{
		int height = 1;
		this.data = null;
		nextNodes = new ArrayList<NodeTest<AnyType>>();
		for (int i = 0; i < height; i++)
			nextNodes.add(null);
	}

	NodeTest(AnyType data, int height)
	{
		this.data = data;
		this.height = height;
		nextNodes = new ArrayList<NodeTest<AnyType>>();
		for (int i = 0; i < height; i++)
			nextNodes.add(null);
	}

	public static void main(String[] args)
	{
		NodeTest<Integer> nt = new NodeTest<Integer>();
		System.out.println(nt.nextNodes.get(0));
	}
}
