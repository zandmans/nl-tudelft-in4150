/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class Message implements Serializable {
	public int messageID = -1;
	public int id = -1;
	public boolean elected = false;

	public Map<String, Serializable> payload;

	public Message(int id) {
		//synchronized (this) {
			//this.messageID = (++Config.lastMsgID);
			this.id = id;
			this.payload = new HashMap<String, Serializable>();
		//}
	}

	public Message(int id, boolean elected) {
		this(id);
		this.elected = elected;
	}

	@Override
	public String toString() {
		return "" + this.id;
	}
}
