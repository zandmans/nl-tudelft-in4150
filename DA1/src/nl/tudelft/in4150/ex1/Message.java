/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex1;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** This is our message object, which contains:
 *    - Metadata needed for correct implementation (messageID, senderID, vector clock)
 *    - Actual data (called payload). Currently used to test the implementation of this assignment. */
public class Message implements Serializable {
	public int messageID = -1;
	public int senderID = -1;
	public VClock sendTime;

	public Map<String, Serializable> payload;

	/** isDeliverable condition as defined on page 32 of Ch 3 of the lecture notes (D_j(m) */
	public boolean isDeliverable(VClock clockReceiver) {
		for (int i=0; i<Config.CLIENT_COUNT; i++)
			if (clockReceiver.get(i) + (i==this.senderID ? 1 : 0) < sendTime.get(i)) return false;
		return true;
	}

	public Message(int senderID, VClock time) {
		synchronized (this) {
			this.messageID = (++Config.lastMsgID);
		}
		this.senderID = senderID;
		this.sendTime = new VClock(time);
		this.payload = new HashMap<String, Serializable>();
	}

	public String toString() {
		return "" + this.messageID;
	}
}
