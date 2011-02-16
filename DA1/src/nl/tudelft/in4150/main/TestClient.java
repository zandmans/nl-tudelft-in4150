/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.main;

import java.util.*;

public class TestClient extends RMIClient implements Runnable {
	private boolean running; /* Running state of the text client; assigning false (while running) will cause clean termination of the thread. */
	private List<Message> buffer;	/* The buffer where buffered message will be saved */
	private VectorClock sendTime = new VectorClock();
	private List<Message> delivered;

	/** Create a new client */
	public TestClient(int clientID) throws java.rmi.RemoteException {
		super(clientID);

		this.buffer = new ArrayList<Message>();
		this.delivered = new ArrayList<Message>();

		for (int i = 0; i < Config.CLIENT_COUNT; ++i) this.sendTime.put(i, 0); // init VectorClock

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
			Message message = new Message(this.clientID);	// create m
			this.sendTime.incrementClock(this.clientID); // V <- V + e_i
			message.sendTime = this.sendTime;	// set V
			this.broadcastMessage(message); // broadcast(m,V)
		}
	}

	/** Broadcasts a message to all known clients, and outputs a notification. */
	public void broadcastMessage(Message m) {
		//System.out.println("==> BROADCASTING FROM " + this.clientID + " TO CLIENTS 0-" + (Config.CLIENT_COUNT - 1) + " WITH MESSAGE ID " + m.messageID);
		for (int i = 0; i < Config.CLIENT_COUNT; i++)
			if(i != this.clientID) this.sendMessage(m, i); // only send to other clients
	}

	/** Show data when it is received. */
	public void onMessageReceived(Message message) {
		// TODO: Implement some reply mechanism here.

		if(this.clientID == 1) {
			System.out.println("<== REC at CLIENT " + this.clientID + " MSG " + message.messageID + " FROM " + message.senderID);
			System.out.println("VectorClock CLIENT " + message.senderID + " : " + message.sendTime.toString());
			System.out.println("VectorClock RECEIVER : " + this.sendTime.toString());
		}

		if(VectorClock.isDeliverable(this.sendTime, message.sendTime, message.senderID)) {	// D_j(m)
			this.deliver(message);
			this.processBuffer();
		} else {
			this.buffer.add(message);
			if(this.clientID == 1) System.out.println("(++) MSG " + message.messageID + " added to BUFFER of CLIENT " + this.clientID);
		}
	}

	/** Process the message */
	public void deliver(Message message) {
		// TODO: Implement a procedure done after receiving the right message
		this.delivered.add(message); // deliver(m)
		this.sendTime.incrementClock(message.senderID); // V <- V + e_j
		if(this.clientID == 1) System.out.println("(--) DELIVERED by CLIENT " + this.clientID + ": " + this.delivered.toString());
	}

	/** Check the buffer for messages which could be delivered */
	public void processBuffer() {
		for(Iterator iterator = this.buffer.iterator(); iterator.hasNext();) {
			Message m = (Message)iterator.next();
			if (VectorClock.isDeliverable(this.sendTime, m.sendTime, m.senderID)) { // D_j(m)
				deliver(m);
				iterator.remove();
			}
		}
		if(this.clientID == 1) System.out.println("(BF) BUFFER of CLIENT " + this.clientID + ": " + this.buffer.toString());	// For testing purposes
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		initializeRMI(Config.REGISTRY_PORT);

		try { for (int i = 0; i < Config.CLIENT_COUNT; ++i) new TestClient(i); }
		catch (Exception e) { e.printStackTrace(); }
	}

}
