package nl.tudelft.in4150.main;

import nl.tudelft.in4150.main.Config;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
	public int messageID = -1;
	public int senderID = -1;
	//public VectorClock sendTime;

	public Map<String, Serializable> payload;

	public Message(int senderID) {
		this.messageID = (++Config.lastMsgID);
		this.senderID = senderID;
	}
}
