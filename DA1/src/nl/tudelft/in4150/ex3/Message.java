/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class Message implements Serializable {
	public int id = -1;

	public int faults = 0;
	public int initialValue = -1;
	public int commander = 1;
	public int[] lieutenants = new int[Config.CLIENT_COUNT];


	public Map<String, Serializable> payload;

	public Message(int id) {
		//synchronized (this) {
			//this.messageID = (++Config.lastMsgID);
			this.id = id;
			this.payload = new HashMap<String, Serializable>();
		//}
	}

	public Message(int msgID, int f, int v, int c, int[] i) {
		this.id = msgID;				// set the message id, to define the round
		this.faults = f; 				// the maximum number of failing processors
		this.initialValue = v;	// the initial value in the commander
		this.commander = c;			// the commander
		this.lieutenants = i;				// the lieutenants...TODO: has to contain the lieutenants through which is has passed
	}

	@Override
	public String toString() {
		return "f: " + this.faults + " i:" + this.initialValue + " clients:" + this.lieutenants.toString();
	}
}
