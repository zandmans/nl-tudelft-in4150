/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex1;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
	public int messageID = -1;
	public int senderID = -1;
	public VClock sendTime;

	public Map<String, Serializable> payload;

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
	}

	public String toString() {
		return "" + this.messageID;
	}
}
