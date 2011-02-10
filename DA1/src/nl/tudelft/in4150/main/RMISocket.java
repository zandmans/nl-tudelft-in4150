package nl.tudelft.in4150.main;

import java.rmi.RMISecurityManager;

import nl.tudelft.in4150.objects.*;

public class RMISocket extends Socket {

	/* Id of the current socket */
	private String id = null;
	
	/* URL of the current socket */
	private String address;
	
	static {
		try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
		}
		catch( Exception e ) {
			throw new Error( e );
		}
	};
	
	/**
	 * Create a new RMI socket.
	 */
	public RMISocket() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
	}
	
	/**
	 * Send the actual message to the given URL.
	 * 
	 * @param message to send.
	 * @param URL to deliver the message to.
	 */
	public void sendMessage(Message message, String URL) {
		String tmpAddress = createAddress(URL);
		message.put("origin", getURL());
		try {
			IRMIMessenger addressee = (IRMIMessenger) java.rmi.Naming.lookup(tmpAddress);
			addressee.receiveMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Receive a message. 
	 *  
	 * @param message is the actual message being received.
	 * 
	 * @see Message
	 * @see Message.send
	 */
	public void receiveMessage(Message message) {
		notifyReceivedHandlers(message, null);
	}
	
	RMIMessenger blaat;
	
	/**
	 * Register the socket, useful for some 
	 * implementations.
	 * 
	 * @param URL 
	 *   is the URL where the socket is to 
	 *   be registered.
	 */
	public void register(String URL) {
		id = URL;
		address = createAddress(id);
		try {
			blaat = new RMIMessenger( this );
			java.rmi.Naming.bind(address, blaat);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unregister the socket. 
	 */
	public void unRegister() {
		if (id != null) {
			try {
				java.rmi.Naming.unbind(address);
			} catch (Exception e) {
				e.printStackTrace();
			}
			id = null;
		}
	}

	/**
	 * @return the URL of the socket.
	 */
	public String getURL() {
		return id;
	}
	
	public String createAddress(String id) {
		return("rmi://localhost/"+id);
	}

}
