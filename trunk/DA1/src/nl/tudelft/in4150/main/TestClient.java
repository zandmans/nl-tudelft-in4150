/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex1;

import java.util.*;

public class TestClient extends RMIClient implements Runnable {
	private boolean running; /* Running state of the text client; assigning false (while running) will cause clean termination of the thread. */
	private List<Message> buffer;	/* The buffer where buffered message will be saved */
	private List<Message> delivered;
	private VClock currentTime;

	/** Create a new client */
	public TestClient(int clientID) throws java.rmi.RemoteException {
		super(clientID);

		this.buffer = new ArrayList<Message>();
		this.delivered = new ArrayList<Message>();

		currentTime = new VClock();

		new Thread(this).start();
		synchronized (this) { // Access to global config
			Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, clientID); // Let all others know this new client exists. (Simple version)
		}
	}

	/** Repeat the process of sending a message to a random node and then sleeping for a configured delay time. */
	public void run() {
		this.running = true;

		try { Thread.sleep(1500); }
		catch (InterruptedException e) { e.printStackTrace(); } // Wait for intializement

		while (running) {
			try { Thread.sleep(Config.CLIENT_DELAY[this.clientID]); } // Wait for some delay
			catch (InterruptedException e) { e.printStackTrace(); }

			Message msg;
			synchronized(this) { // Access to VClock
				this.currentTime.inc(this.clientID);
				msg = new Message(this.clientID, this.currentTime);	// create m
			}

			this.broadcastMessage(msg); // broadcast(m,V)
		}
	}

	/** Broadcasts a message to all known clients, and outputs a notification. */
	public synchronized void broadcastMessage(Message m) {
		if (Config.DEBUG_SEND) System.out.println(">>>> SEND " + m.messageID + m.sendTime.toString() + " @ " + this.clientID + " with buffer " + this.buffer.toString());
		for (int i = 0; i < Config.CLIENT_COUNT; i++)
			if (m.senderID != i) this.sendMessage(m, i);
	}

	/** Show data when it is received. */
	public void onMessageReceived(Message msg) {
		if (msg.isDeliverable(this.currentTime)) {
			this.deliver(msg);
			this.processBuffer();
		} else {
			synchronized (this) { // Access to buffer
				this.buffer.add(msg);
			}
		}
	}

	/** Process the message */
	private void deliver(Message msg) {
		synchronized (this) { // Access to clock and delivered
			this.delivered.add(msg); // deliver(m)
			this.currentTime.inc(msg.senderID); // V <- V + e_j
		}
		if (Config.DEBUG_RECV) System.out.println("(--) RECV " + msg.messageID+this.currentTime.toString() + " @ " + this.clientID);


		// TODO: Implement a procedure done after receiving the right message
	}

	/** Check the buffer for messages which could be delivered */
	private synchronized void processBuffer() {
		List<Message> unprocessed = new ArrayList<Message>();
		for (Message m : this.buffer) {
			if (m.isDeliverable(this.currentTime)) deliver(m);
			else unprocessed.add(m);
		}
		this.buffer = unprocessed;
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		initializeRMI(Config.REGISTRY_PORT);

		try { for (int i = 0; i < Config.CLIENT_COUNT; ++i) new TestClient(i); }
		catch (Exception e) { e.printStackTrace(); }
	}

}
