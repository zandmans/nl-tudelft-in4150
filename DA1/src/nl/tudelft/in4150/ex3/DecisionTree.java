package nl.tudelft.in4150.ex3;

import java.util.LinkedList;
import java.util.ArrayList;

public class DecisionTree {
	public int node;	// Process ID
	public int value; // Value received along path
	public int level; // Current level
	public DecisionTree parent;
	public ArrayList<DecisionTree> children;

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
		DecisionTree currentParent = null;
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

	// Recursive function te complete tree to current level
	/*
	public static void HandleMissingMessages(Component caller, DecisionTree treeNode, LinkedList<Integer> currentPath, ArrayList<Integer> missingSenders) {
		LinkedList<Integer> copyPath;
		if (!treeNode.children.isEmpty()) { // This shouldn't be necessary, but just in case.
			if (currentPath.size() == caller.currentRound) {
				// Insert child to this node.
				for(Integer missingSender : missingSenders) {
					if (Config.OUTPUT_DEBUGDATA > -1) System.out.println("I ("+caller.clientID+") have forged non-received message from "+missingSender);
					copyPath = new LinkedList<Integer>(currentPath);
					copyPath.add(missingSender);
					treeNode.children.add(new DecisionTree(missingSender, Config.DEFAULT_VALUE, copyPath.size()-1, treeNode));
					if (caller.currentRound < caller.maxFaults && !currentPath.contains(caller.clientID)) {
						copyPath.add(caller.clientID);
						caller.roundMessage.AddSubMsg(copyPath, Config.DEFAULT_VALUE);
					}
				}
			} else {
				// Recurse because we haven't reached desired depth level yet
				for(DecisionTree currentChild : treeNode.children) {
					copyPath = new LinkedList<Integer>(currentPath);
					copyPath.add(currentChild.node);
					HandleMissingMessages(caller, currentChild, copyPath, missingSenders);
				}
			}
		} else {
			if (Config.OUTPUT_DEBUGDATA > -1) {
				String x;
				x =(" -- Start afscheidsbrief --");
				x+=("\nUhh, this is strange?");
				x+=("\nI am "+caller.clientID);
				x+=("\nMy current round is "+caller.currentRound);
				x+=("\nMy root node is "+caller.data.node);
				x+=("\nMy current tree node is "+treeNode.node);
				x+=("\nMy current path in tree is "+currentPath);
				x+=("\nAnd my current tree node doesn't seem to have children, which it is supposed to.");
				x+=("\n -- Bye bye..... * slits throat * --");
				x+=("\nPS (Post Suicide): my data tree was like.."+caller.data);
				System.out.println(x);
				System.exit(1);
			}
		}
	}
	*/
	//public static void HandleMissingMessages_(Message roundMessage, DecisionTree treeNode, LinkedList<Integer> currentPath, ArrayList<Integer> missingSenders) {}

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