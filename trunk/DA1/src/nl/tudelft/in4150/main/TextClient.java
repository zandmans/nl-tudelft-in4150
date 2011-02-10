/**
 * Software written for the Distributed Systems Lab course.
 *
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.main;

import nl.tudelft.in4150.objects.IMessageReceivedHandler;
import nl.tudelft.in4150.objects.Message;
import nl.tudelft.in4150.objects.Socket;
import nl.tudelft.in4150.objects.SynchronizedSocket;

import java.util.Random;

public class TextClient implements Runnable, IMessageReceivedHandler {

	private boolean useSynchronizedSocket = true;
	private int clientID;					 /* Unique identifier of this text client */
	private String id;							/* Local URL */
	private int customDelay;				/* Time it takes for a message to be send */
	private boolean running;				/* Running state of the text client */
	private Random random = new Random(System.currentTimeMillis());/* A random number generator */
	private Socket socket;					/* Socket over which to send data */

	// TODO: Implement Lamport clocks

	/**
	 * Create a new textclient, utilizing a random delay.
	 *
	 * @param ClientID
	 */
	public TextClient(int ClientID) {
		super();
		this.clientID = ClientID;

		//LocalSocket ls = new LocalSocket();
		RMISocket ls = new RMISocket();

		id = "" + ClientID;

		if (useSynchronizedSocket) socket = new SynchronizedSocket(ls);
		else socket = ls;

		ls.register(id);
		this.socket.addMessageReceivedHandler(this);

		this.customDelay = Config.CLIENT_DELAY[ClientID];

		new Thread(this).start();
		Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, ClientID);
	}

	/**
	 * Repeat the process of sending a
	 * message to a random node and then
	 * sleeping for a given delay time.
	 */
	public void run() {
		this.running = true;

		// Wait for intializement
		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

		while (running) {
			try {
				Thread.sleep(customDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/**
			 * Show a message when it is being send.
			 */
			Message m = new Message();
			//int target = Math.abs(random.nextInt() % Config.CLIENT_INIT-1);
			System.out.println("BROADCASTING FROM " + id + " WITH MESSAGE ID " + m.getMessageID());
			for (int i = 0; i < Config.CLIENT_COUNT; ++i) {
				//System.out.println("SND TO "+i+" FRM "+id+" MSGID " + m.getMessageID());
				socket.sendMessage(m, String.valueOf(i));
			}
		}

		socket.unRegister();
	}

	/**
	 * Create three textclients.
	 */
	public static void main(String[] args) {
		for (int i = 0; i < Config.CLIENT_COUNT; ++i) new TextClient(i);
	}

	/**
	 * Show data when it is received.
	 */
	public void onMessageReceived(Message message) {
		// TODO: Implement some reply mechanism here.
		System.out.println("REC AT " + id + " MSGID " + message.getMessageID());
	}

}
