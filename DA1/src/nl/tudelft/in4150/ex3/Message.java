/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
	public int faults = 0;
	public int value = -1;
	public int commander = 1;
	public ArrayList<Integer> lieutenants;

	public Message(int f, int v, int c, ArrayList<Integer> i) {
		this.faults = f; 				  // the maximum number of failing processors left
		this.value = v;	          // the value of the order by the current commander
		this.commander = c;			  // the current commander
		this.lieutenants = (ArrayList<Integer>)i.clone();				// the lieutenants...
	}

	@Override
	public String toString() {
		return "f: " + this.faults + " i:" + this.value + " clients:" + this.lieutenants.toString();
	}
}
