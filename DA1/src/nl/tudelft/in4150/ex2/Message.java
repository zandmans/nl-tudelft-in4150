/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex2;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
	public int messageID = -1;
	public int id = -1;
	public int type = -1;

	public Map<String, Serializable> payload;

	public Message(int id, int type) {
		synchronized (this) {
			this.messageID = (++Config.lastMsgID);
			this.id = id;
			this.type = type;
		}
	}

	@Override
	public String toString() {
		return "" + this.id;
	}
}
