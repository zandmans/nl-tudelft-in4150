package nl.tudelft.in4150.ex3;

import java.util.LinkedList;
import java.util.ArrayList;

public class DecisionTree {
	public int node;	// Process ID
	public int value; // Value received along path
	public ArrayList<DecisionTree> children;

	// Creates a new node.
	public DecisionTree(int node, int value) {
		this.children = new ArrayList<DecisionTree>();
		this.node = node;
		this.value = value;
	}

	// Adds a new node at the right leaf of this tree, according to the path.
	public void AddNewNode(LinkedList<Integer> pathIn, int value) {
		LinkedList<Integer> path = new LinkedList<Integer>(pathIn); // Copy
		DecisionTree newNode = new DecisionTree(path.removeLast(), value);

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
		currentLeaf.children.add(newNode);
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
	public static void HandleMissingMessages(Component caller, DecisionTree treeNode, LinkedList<Integer> currentPath, ArrayList<Integer> missingSenders) {
		LinkedList<Integer> copyPath;
		if (!treeNode.children.isEmpty()) { // This shouldn't be necessary, but just in case.
			if (treeNode.children.get(0).children.isEmpty()) {
				// Insert child to this node.
				//HandleMissingMessages_(roundMessage, treeNode, currentPath)
				for(Integer missingSender : missingSenders) {
					if (Config.OUTPUT_DEBUGDATA > 0) System.out.println("I ("+caller.clientID+") have forged non-received message from "+missingSender);
					treeNode.children.add(new DecisionTree(missingSender, Config.DEFAULT_VALUE));
					copyPath = new LinkedList<Integer>(currentPath);
					copyPath.add(missingSender);
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
			if (Config.OUTPUT_DEBUGDATA > 0) System.out.println("Uhh, this is strange?");
		}
	}
	//public static void HandleMissingMessages_(Message roundMessage, DecisionTree treeNode, LinkedList<Integer> currentPath, ArrayList<Integer> missingSenders) {}

	// Majority function
	public static int Majority(ArrayList<Integer> a) {
		int currentMax = 0;
		int count[] = new int[10];
		for (Integer i : a) count[i]++;
		for (int i=1; i<10; i++) if (count[i] > count[currentMax]) currentMax = i;
		return currentMax;
	}
}
