/**
 * Software written for the Distributed Systems Lab course.
 * 
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.objects;

import java.util.ArrayList;

public class SynchronizedSocket extends Socket implements Runnable, IMessageReceivedHandler {
	private boolean running;
	private ArrayList<Message> waitQueue;
	private Thread runningThread;
	private Socket socket;
	private String url;
	private static int uniqueID = 1234;
	private double messageDelay;
	
	/**
	 * Create a new synchronized socket. Base
	 * this on the given socket, listening
	 * for messages received on the given 
	 * socket and making sure they appear 
	 * in order.
	 * 
	 * @param socket is the actual socket
	 * to which data is being sent.
	 */
	public SynchronizedSocket(Socket socket) {
		this.waitQueue = new ArrayList<Message>();
		this.socket = socket;
		setMessageDelay(Math.random() / 100.0);
		runningThread = new Thread(this);
		runningThread.start();
		
		register(""+(++uniqueID));
		
		socket.addMessageReceivedHandler(this);
	}

	/**
	 * Set the delay it takes a message
	 * to be received. 
	 * 
	 * @param newDelay is the time in seconds.
	 */
	public synchronized void setMessageDelay(double newDelay)
	{
		messageDelay = newDelay;
	}
	
	/**
	 * Wake up the current thread.
	 */
	public void wakeUp() {
		runningThread.interrupt();
	}

	/**
	 * Useful check to see wether there are any
	 * messages waiting in the buffer.
	 * 
	 * @return 
	 *   wether there are any messages left in 
	 *   the message queue.
	 */
	public synchronized boolean hasMessagesWaiting() {
		return !waitQueue.isEmpty();
	}
	
	/**
	 * @return the URL from the underlying
	 * socket class.
	 */
	public String getURL() {
		return socket.getURL();
	}
	
	/**
	 * Part of the run cycle which
	 * needs to be thread-safe.
	 */
	public void doRun()
	{
		Message m = null;
		while( true ) {
			synchronized( this ) {
				/* Check if any messages are waiting
				 * in the queue.
				 */
				if (waitQueue.size() > 0)
				{				
					/* Get the one which was sent first
					 * TODO: sort a queue in O(n log n) time
					 * and trace thru this. Much quicker than
					 * O(n^3).
					 */
					m = getMostRecentMessage();
					if( m.getSendTime() >= (SocketMonitor.getInstance().currentTime - messageDelay) )
						m = null;
				}
				if( m == null )
					break;
				waitQueue.remove( m );
			}
			notifyReceivedHandlers(m,this);
			m = null;
		}
	}

	/**
	 * The main proces in the SynchronizedSocket. This
	 * is being activated when an interrupt is made on the
	 * running thread. It will then deliver any messages
	 * in the buffer in order of sendtime.
	 */
	public void run() {
		running = true;
		
		while(running)
		{
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				/* Wake up on interrupt */
			}
			
			doRun();
		}
	}
	
	/**
	 * Register the synchronized socket with the socketmonitor.
	 * 
	 * @pre The ID has not been registered yet with the socketmonitor.
	 * @post The ID has been registrated with the socketmonitor. 
	 */
	public void register(String URL)
	{
		/* Store the url */
		this.url = URL;		
		SocketMonitor.getInstance().registerSocket(URL, this);
		socket.register(URL);		
	}
	
	/**
	 * Unregister the synchronized socket with the socketmonitor.
	 * 
	 * @pre The ID has been registrated with the socketmonitor. 
	 * @post The ID has not been registered yet with the socketmonitor.
	 */
	public void unRegister()
	{
		running = false;
		SocketMonitor.getInstance().unRegisterSocket(url);
		socket.unRegister();
		socket = null;
		wakeUp();
	}
	
	/**
	 * Send a message to the specified targetID.
	 * 
	 * @param m is the message to send.
	 * @param URL is the id to which the message is to be delivered.
	 */
	public void sendMessage(Message m, String URL)
	{
		/* Make sure the sendtime is correct */
		m.send(URL);
		m.put("origin", getURL());
		
		socket.sendMessage(m, URL);
	}

	/**
	 * Get the message, which was sent first.
	 *  
	 * @return the message which has been sent first.
	 */
	public synchronized Message getMostRecentMessage()
	{
		Message res = null;
		
		for(Message it: waitQueue)
		{
			if (res == null || it.getSendTime() < res.getSendTime())
				res = it;
		}
				
		return res;		
	}
	
	/**
	 * Queue the message for delivery in the current socket. 
	 * @param message is the message to queue.
	 */
	public synchronized void onMessageReceived(Message message) {
		//System.out.println("recieved @ SyncSoc");
		waitQueue.add(message);
	}

	@Override
	public synchronized void receiveMessage(Message message) {}

}
