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
		this.paths = new ArrayList<LinkedList<Integer>>();
		this.values = new ArrayList<Integer>();
	}

	public void AddSubMsg(LinkedList<Integer> path, int value) {
		this.paths.add((LinkedList<Integer>)path.clone());
		this.values.add(value);
	}

	public String toString() {
		String s = "[In "+this.currentRound+" from "+this.sender+" ";
		for (int i=0; i<this.paths.size(); i++) {
			s += " : <"+values.get(i)+"|";
			for (int j=0; j<this.paths.get(i).size(); j++)
				s += ","+this.paths.get(i).get(j);
			s += ">";
		}
		s += "]";
		return(s);
	}
}
