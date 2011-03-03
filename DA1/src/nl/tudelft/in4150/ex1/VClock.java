/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex1;

import java.io.Serializable;
import java.util.ArrayList;

/** Very simple implementation of a vector clock. Only the very (necessary) basic functions are implemented. */
public class VClock implements Serializable {
	private ArrayList<Integer> vclock;

	public VClock() {
		this.vclock = new ArrayList<Integer>(Config.CLIENT_COUNT);
		for (int i=0; i<Config.CLIENT_COUNT; i++)
			this.vclock.add(0);
	}

	/** Returns a copy of parameter-passed clock! Used to prevent pass-by-reference. */
	public VClock(VClock vclock) {
		this.vclock = new ArrayList<Integer>(Config.CLIENT_COUNT);
		for (int i=0; i<Config.CLIENT_COUNT; i++)
			this.vclock.add(vclock.get(i));
	}

	/** Returns the value at position index (V_i) */
	public int get(int index) {
		return(this.vclock.get(index));
	}

	/** Increments the value at position index (V_i) by 1 */
	public void inc(int index) {
		this.vclock.set(index, this.vclock.get(index)+1);
	}

	/** Returns a string representation {..;..;..} of this vector clock. */
	public String toString() {
		String out = "{";
		for (int i=0; i<Config.CLIENT_COUNT; i++)
			out += String.format("%1$#"+2+"s;", this.vclock.get(i));
		return(out.substring(0, out.length() - 1)+"}");
	}
}
