/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Message implements Serializable {
	public int currentRound;
	public int sender;
	public ArrayList<LinkedList<Integer>> paths;
	public ArrayList<Integer> values;

	public Message(int currentRound, int sender) {
		this.currentRound = currentRound; // the maximum number of failing processors left
		this.sender = sender;	// the value of the order by the current commander

	}

	public void AddSubMsg(LinkedList<Integer> path, int value) {
		this.paths.add((LinkedList<Integer>)path.clone());
		this.values.add(value);
	}
}
