/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.main;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
	public int messageID = -1;
	public int senderID = -1;
	public VectorClock sendTime;

	public Map<String, Serializable> payload;

	public Message(int senderID) {
		synchronized (this) {
			this.messageID = (++Config.lastMsgID);
		}
		this.senderID = senderID;
	}
}
