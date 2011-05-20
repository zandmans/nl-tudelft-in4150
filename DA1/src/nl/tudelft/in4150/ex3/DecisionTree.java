package nl.tudelft.in4150.ex3;

import java.util.LinkedList;
import java.util.ArrayList;

public class DecisionTree implements DecisionObject {
	private int node;	// Process ID
	private int value; // Value received along path
	private int level; // Current level
	private DecisionTree parent;
	private ArrayList<DecisionTree> children;

	// Creates a new node.
	public DecisionTree(int node, int value) {
		this(node, value, 1, null);
	}

	// Creates a new node.
	public DecisionTree(int node, int value, int level, DecisionTree parent) {
		this.children = new ArrayList<DecisionTree>();
		this.node = node;
		this.value = value;
		this.level = level;
		this.parent = parent;
	}

	// Adds a new node at the right leaf of this tree, according to the path.
	public void AddNewNode(LinkedList<Integer> pathIn, int value) {
		LinkedList<Integer> path = new LinkedList<Integer>(pathIn); // Copy
		int node = path.removeLast();

		int currentNode = path.removeFirst();
		DecisionTree currentLeaf = this;
		assert(this.node == currentNode);

		while (!path.isEmpty()) {
			currentNode = path.removeFirst();
			for (int i=0; i<currentLeaf.children.size(); i++)
				if (currentLeaf.children.get(i).node == currentNode) {
					currentLeaf = currentLeaf.children.get(i);
					break;
				}
		}
		currentLeaf.children.add(new DecisionTree(node, value, pathIn.size(), currentLeaf));
	}

	// Recursive decide function
	public int Decide() {
		if (this.children.isEmpty()) return(this.value);
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (DecisionTree child : this.children)
			values.add(child.Decide());
		return(DecisionTree.Majority(values));
	}

	// Majority function
	public static int Majority(ArrayList<Integer> a) {
		int currentMax = 0;
		int count[] = new int[10];
		for (Integer i : a) count[i]++;
		for (int i=1; i<10; i++) if (count[i] > count[currentMax]) currentMax = i;
		return currentMax;
	}

	public String toString() {
		String out = "Print tree:";
		LinkedList<DecisionTree> Q = new LinkedList<DecisionTree>();
		DecisionTree node;
		Q.add(this);
		while (!Q.isEmpty()) {
			node = Q.removeFirst();
			out += "\nNode @ depth="+node.level+" from parent="+(node.parent != null ? node.parent.node : "NONE")+" to me="+node.node+" with value="+node.value;
			for (DecisionTree child : node.children) {
				Q.add(child);
			}
		}
		return(out);
	}
}