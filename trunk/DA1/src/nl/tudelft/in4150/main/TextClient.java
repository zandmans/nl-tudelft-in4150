/**
 * Software written for the Distributed Systems Lab course.
 * 
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.main;

import java.util.Random;

import nl.tudelft.in4150.objects.*;

public class TextClient implements Runnable, IMessageReceivedHandler {

	private boolean useSynchronizedSocket = true;
	
	/* Unique identifier of this text client */
	private static int uniqueID = 0; 	
	/* Local URL */
	private String id;
	/* Time it takes for a message to be send */
	private int customDelay;
	/* Running state of the text client */
	private boolean running;
	/* A random number generator */
	private Random random = new Random(System.currentTimeMillis());
	/* Socket over which to send data */
	private Socket socket;
	
	/**
	 * @return a unique identifier, identifying this
	 * textclient.
	 */
	private synchronized static int getUniqueID() {
		return uniqueID++;
	}
	
	/**
	 * Create a new textclient, utilizing a
	 * random delay.
	 * 
	 * @param delay it takes to send a message
	 * to a random machine.
	 */
	public TextClient(int delay)
	{
		super();
		
		//LocalSocket ls = new LocalSocket();
		RMISocket ls = new RMISocket();
		
		id = ""+(TextClient.getUniqueID());
		
		if (useSynchronizedSocket)
			socket = new SynchronizedSocket(ls);
		else
			socket = ls;
		
		ls.register(id);
		socket.addMessageReceivedHandler(this);		

		customDelay = delay;
				
		new Thread(this).start();
	}
	
	/**
	 * Repeat the process of sending a 
	 * message to a random node and then
	 * sleeping for a given delay time.
	 */
	public void run()
	{
		running = true;
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(running)
		{
			try {
				Thread.sleep(customDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			/**
			 * Show a message when it is being send.
			 */
			Message m = new Message();
			int tgt = Math.abs(random.nextInt() % uniqueID);
			System.out.println("SND TO "+tgt+" FRM "+id+" MSGID " + m.getMessageID());
			socket.sendMessage(m, String.valueOf(tgt));			
		}
		
		socket.unRegister();
	}
	
	/**
	 * Create three textclients.
	 */
	public static void main(String[] args)
	{
		for(int i = 0; i < 3; ++i) 
			new TextClient(100*(i+1));
	}

	/**
	 * Show data when it is received.
	 */
	public void onMessageReceived(Message message) {	
		System.out.println("REC AT "+id+" MSGID "+message.getMessageID());
	}
	
}
