/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.main;

import java.util.ArrayList;
import java.util.List;

public class TestClient extends RMIClient implements Runnable {
	private boolean running; /* Running state of the text client; assigning false (while running) will cause clean termination of the thread. */
	private List<Message> buffer;	/* The buffer where buffered message will be saved */
	private VectorClock sendTime = new VectorClock();

	/** Create a new client */
	public TestClient(int clientID) throws java.rmi.RemoteException {
		super(clientID);

		this.buffer = new ArrayList<Message>();

		new Thread(this).start();
		synchronized (this) {
			Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, clientID); // Let all others know this new client exists. (Simple version)
		}
	}

	/** Repeat the process of sending a message to a random node and then sleeping for a configured delay time. */
	public void run() {
		this.running = true;

		try { Thread.sleep(500); }
		catch (InterruptedException e) { e.printStackTrace(); } // Wait for intializement

		while (running) {
			try { Thread.sleep(Config.CLIENT_DELAY[this.clientID]); } // Wait for some delay
			catch (InterruptedException e) { e.printStackTrace(); }
			Message message = new Message(this.clientID);
			this.sendTime.incrementClock(Integer.toString(this.clientID));
			message.sendTime = this.sendTime;
			this.broadcastMessage(message); // Broadcast new message.
		}
	}

	/** Broadcasts a message to all known clients, and outputs a notification. */
	public void broadcastMessage(Message m) {
		System.out.println("BROADCASTING FROM " + this.clientID + " TO CLIENTS 0-" + (Config.CLIENT_COUNT - 1) + " WITH MESSAGE ID " + m.messageID);
		for (int i = 0; i < Config.CLIENT_COUNT; i++)
			this.sendMessage(m, i);
	}

	/** Show data when it is received. */
	public void onMessageReceived(Message message) {
		// TODO: Implement some reply mechanism here.
		System.out.println("REC AT " + this.clientID + " MSGID " + message.messageID);

		if (VectorClock.compare(this.sendTime, message.sendTime) != VectorComparison.SMALLER) {
			this.sendTime = VectorClock.max(this.sendTime, message.sendTime);
			// System.out.println(this.sendTime.toString());	// For testing purposes
			this.deliver(message);
			this.processBuffer();
		} else
			this.buffer.add(message);
	}

	/** Process the message */
	public void deliver(Message message) {
		// TODO: Implement a procedure done after receiving the right message
		System.out.println("MSGID " + message.messageID + " processed by " + this.clientID);
	}

	/** Check the buffer for messages which could be delivered */
	public void processBuffer() {
		// String strBuffer = "";	// For testing purposes
		for (Message m : this.buffer) // Check all the messages in the buffer
		{
			// strBuffer += m.messageID + ", ";	// For testing purposes
			if (VectorClock.compare(this.sendTime, m.sendTime) != VectorComparison.SMALLER) // The condition for delivering the message from the buffer
				deliver(m);
		}
		// System.out.println(strBuffer);	// For testing purposes
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		initializeRMI(Config.REGISTRY_PORT);

		try { for (int i = 0; i < Config.CLIENT_COUNT; ++i) new TestClient(i); }
		catch (Exception e) { e.printStackTrace(); }
	}

}
