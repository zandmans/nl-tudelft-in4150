/**
 * Software written for the Distributed Systems Lab course.
 * 
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.main;

import nl.tudelft.in4150.objects.*;
import nl.tudelft.in4150.exception.*;

import java.util.HashMap;
import java.util.Map;

public class LocalSocket extends Socket {

	/* ID of the current socket */
	private String id;
	/* Hashmap where ids are stored */
	private static Map<String, Socket> localMap = new HashMap<String, Socket>();
	
	/**
	 * Create a new local socket.
	 */
	public LocalSocket()
	{
	}
	
	/**
	 * Register the socket with the hashmap.
	 */
	@Override
	public void register(String URL) {
		id = URL;
		/* TODO add a check wether the slot is taken? */
		localMap.put(id, this);
	}

	/**
	 * Notify handlers that a message has arrived.
	 */
	public void receiveMessage(Message message) {
		notifyReceivedHandlers(message, null);
	}

	/**
	 * Unregister the socket 
	 */
	@Override
	public void unRegister() {
		localMap.remove(id); 
	}
	
	/**
	 * @return this URL.
	 */
	public String getURL() {
		return id;
	}

	/**
	 * Send a message to the specified URL.
	 * 
	 * @param message is the message to send.
	 * @param URL is the target address to send it to.
	 */
	@Override
	public void sendMessage(Message message, String URL) {
		if (localMap.containsKey(URL))
		{
			message.put("origin", getURL());
			localMap.get(URL).receiveMessage(message);
		}
		else
			throw new IDNotAssignedException();
	}
}
