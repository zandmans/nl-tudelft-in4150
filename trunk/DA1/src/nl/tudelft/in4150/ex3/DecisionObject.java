package nl.tudelft.in4150.ex3;

import java.util.LinkedList;

public interface DecisionObject {
	public void AddNewNode(LinkedList<Integer> pathIn, int value);
	public int Decide();
	public String toString();
}
