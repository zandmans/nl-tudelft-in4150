package nl.tudelft.in4150.ex3;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class DecisionMap implements DecisionObject {
	private int faults;
	private int clients;
	private Hashtable<LinkedList<Integer>, Integer> msgs;

	// Creates a new node.
	public DecisionMap(int faults, int clients) {
		this.faults = faults;
		this.clients = clients;
		msgs = new Hashtable<LinkedList<Integer>, Integer>();
	}

	public void AddNewNode(LinkedList<Integer> pathIn, int value) {
		if (pathIn.size() == this.faults+1) {
			LinkedList<Integer> path = (LinkedList<Integer>)pathIn.clone();
			this.msgs.put(path, value);
		}
	}

	// Recursive decide function
	public int Decide() {
		if (msgs.size() == 1) {
			LinkedList<Integer> nullEntry = new LinkedList<Integer>();
			nullEntry.add(0);
			return(msgs.get(nullEntry));
		} else {
			// Prepare
			DecisionMap oneLevelLess = new DecisionMap(this.faults - 1, this.clients);
			Hashtable<LinkedList<Integer>, ArrayList<Integer>> valuesForParentPath = new Hashtable<LinkedList<Integer>, ArrayList<Integer>>();

			// Remove last levels and fetch into arrays
			LinkedList<Integer> prevPath;
			for (Map.Entry<LinkedList<Integer>, Integer> entry : this.msgs.entrySet()) {
				prevPath = entry.getKey();
				prevPath.removeLast();
				if (!valuesForParentPath.containsKey(prevPath))
					valuesForParentPath.put(prevPath, new ArrayList<Integer>());
				valuesForParentPath.get(prevPath).add(entry.getValue());
			}
			this.msgs.clear(); // Free memory, because recursion can suck a lot of memory, especially with these large objects. 

			// Construct oneLevelLess based on valuesForParentPath and the majority of their fetched arrays
			for (Map.Entry<LinkedList<Integer>, ArrayList<Integer>> entry : valuesForParentPath.entrySet())
				oneLevelLess.AddNewNode(entry.getKey(), DecisionMap.Majority(entry.getValue()));

			return(oneLevelLess.Decide());
		}
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
		return("Not implemented yet.");
	}
}