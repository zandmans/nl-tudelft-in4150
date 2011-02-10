/**
 * Software written for the Distributed Systems Lab course.
 * 
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.objects;

import nl.tudelft.in4150.exception.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A message is the actual data unit, which is 
 * transmitted over the transport layer.  
 * This contains both the actual data and relevent 
 * metadata (time this was send, etc).
 */
@SuppressWarnings("serial")
public class Message implements Serializable {
	/* Current global message id in the current JVM */
	private static int messageIDs = 0;
	/* Data elements */
	public Map<String,Serializable> dataElements;
	/* IDs of internal data strings */
	private static final String ID_MESSAGEID = "*MessageID";
	private static final String ID_SENDTIME = "*SendTime";
	private static final String ID_URL = "*URL";
	
	
	/**
	 * Construct a message, assign a unique message
	 * ID (according to the current JVM) and assign
	 * the time at which this message was sent.
	 * 
	 * @pre 
	 *   none
	 * @post 
	 *   message hasBeenSend state is false
	 */
	public Message() {
		dataElements = new HashMap<String,Serializable>();
		dataElements.put(ID_MESSAGEID, Message.getNewMessageID());
		dataElements.put(ID_SENDTIME, -1.0);
	}
	
	/**
	 * @return a unique threadsafe identifier, 
	 * identifying the message.
	 */
	private static synchronized int getNewMessageID() {
		return ++messageIDs;
	}
	
	/**
	 * Call this when the message is about to be
	 * send.
	 * 
	 * @pre 
	 *   message has not been sent
	 *    
	 * @post 
	 *   message has been sent, sendTime has been updated.
	 *
	 * @throws 
	 *   MessageHasAlreadyBeenSendException when the message has 
	 *   already been sent. 
	 */
	public void send(String URL) {
		/*
        if ((Double)dataElements.get(ID_SENDTIME) != -1)
			throw new MessageHasAlreadyBeenSendException();
	    */

		dataElements.put(ID_SENDTIME, SocketMonitor.getInstance().getTime());
		dataElements.put(ID_URL, URL);
	}
	
	
	/**
	 * Return the time at which the message has been sent.
	 * 
	 * @return 
	 *   the time at which the message has been sent.
	 * @pre
	 *   Message has been send.
	 * @throws 
	 *   MessageHasNotBeenSendException when the message has 
	 *   not been send yet (so there is no actual sendtime). 
	 */
	public double getSendTime() {
		if ((Double)dataElements.get(ID_SENDTIME) == -1)
			throw new MessageHasNotBeenSendException();
		
		return (Double)dataElements.get(ID_SENDTIME);
	}
	
	/**
	 * @return true iff the message has been sent.
	 */
	public boolean hasBeenSent() {
		return (Double)dataElements.get(ID_SENDTIME) != -1;
	}
	
	/**
	 * @return the unique message id.
	 */
	public int getMessageID() {
		return (Integer)dataElements.get(ID_MESSAGEID);
	}
	
	/**
	 * @return target URL.
	 */
	public String getURL()
	{
		return (String)dataElements.get(ID_URL);
	}
	
	/**
	 * Insert an element in the dataset.
	 * @param key identifier of the hashmap.
	 * @param value replacing the old value of the key.
	 */
	public void put(String key, Serializable value)
	{
		dataElements.put("_"+key, value);
	}
	
	/**
	 * Retrieve an element from the dataset.
	 * 
	 * @param key identifier.
	 * 
	 * @return the value at the key position.
	 */
	public Serializable get(String key)
	{
		return dataElements.get("_"+key);
	}
}
