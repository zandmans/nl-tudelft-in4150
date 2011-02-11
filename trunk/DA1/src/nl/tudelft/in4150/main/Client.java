/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.main;

import nl.tudelft.in4150.objects.*;

import java.util.Random;

public class Client implements Runnable, IMessageReceivedHandler {
	private int clientID;		 /* Unique identifier of this text client */
	private boolean running; /* Running state of the text client */
	private Random random = new Random(System.currentTimeMillis());/* A random number generator */
	private Socket socket;	 /* Socket over which to send data */

	/** Transfroms internal client ID to string representation (Client URL). */
	public static String transformIDtoURL(int id) {
		return ("" + id); // Implicit cast.
	}

	// TODO: Implement Lamport clocks

	/** Create a new client */
	public Client(int ClientID) {
		super();
		this.clientID = ClientID;

		// Create socket for client.
		this.socket = new RMISocket();
		if (Config.useSynchronizedSockets) this.socket = new SynchronizedSocket(this.socket);
		this.socket.register(transformIDtoURL(this.clientID));
		this.socket.addMessageReceivedHandler(this);

		new Thread(this).start();
		Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, ClientID); // Let all others know this new client exists. (Simple version)
	}

	/** Repeat the process of sending a message to a random node and then sleeping for a configured delay time. */
	public void run() {
		this.running = true;

		try { Thread.sleep(500); }
		catch (InterruptedException e) { e.printStackTrace(); } // Wait for intializement

		while (running) {
			try { Thread.sleep(Config.CLIENT_DELAY[this.clientID]); } // Wait for some delay
			catch (InterruptedException e) { e.printStackTrace(); }
			this.broadcastMessage(new Message()); // Broadcast new message.
		}

		socket.unRegister();
	}

	/** Broadcasts a message to all known clients, and outputs a notification. */
	public void broadcastMessage(Message m) {
		System.out.println("BROADCASTING FROM " + this.id + " TO CLIENTS 0-" + (Config.CLIENT_COUNT - 1) + " WITH MESSAGE ID " + m.getMessageID());
		for (int i = 0; i < Config.CLIENT_COUNT; i++)
			socket.sendMessage(m, String.valueOf(i));
	}

	/** Show data when it is received. */
	public void onMessageReceived(Message message) {
		// TODO: Implement some reply mechanism here.
		System.out.println("REC AT " + id + " MSGID " + message.getMessageID());
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		for (int i = 0; i < Config.CLIENT_COUNT; ++i) new Client(i);
	}

}
