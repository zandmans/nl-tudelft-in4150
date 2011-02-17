package nl.tudelft.in4150.ex1;

import java.io.Serializable;
import java.util.ArrayList;

public class VClock implements Serializable {
	private ArrayList<Integer> vclock;

	public VClock() {
		this.vclock = new ArrayList<Integer>(Config.CLIENT_COUNT);
		for (int i=0; i<Config.CLIENT_COUNT; i++)
			this.vclock.add(0);
	}

	// Returns a copy of parameter-passed clock!
	public VClock(VClock vclock) {
		this.vclock = new ArrayList<Integer>(Config.CLIENT_COUNT);
		for (int i=0; i<Config.CLIENT_COUNT; i++)
			this.vclock.add(vclock.get(i));
	}

	public int get(int index) {
		return(this.vclock.get(index));
	}

	public void inc(int index) {
		this.vclock.set(index, this.vclock.get(index)+1);
	}

	public String toString() {
		String out = "{";
		for (int i=0; i<Config.CLIENT_COUNT; i++)
			out += String.format("%1$#"+2+"s;", this.vclock.get(i));
		return(out.substring(0, out.length() - 1)+"}");
	}
}
