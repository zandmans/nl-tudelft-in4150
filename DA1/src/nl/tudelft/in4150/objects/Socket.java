/**
 * Software written for the Distributed Systems Lab course.
 *
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.objects;

import java.util.ArrayList;

/** Abstract socket class. Should be used to implement different transport types (TCP/IP, RMI, etc). */
public abstract class Socket {
	private ArrayList<IMessageReceivedHandler> receivedHandlers = new ArrayList<IMessageReceivedHandler>();

	/**
	 * Send the actual message to the given URL.
	 * @param message to send.
	 * @param URL		 to deliver the message to.
	 */
	public abstract void sendMessage(Message message, String URL);

	/**
	 * Receive a message.
	 * @param message is the actual message being received.
	 * @see Message
	 * @see Message.send
	 */
	public abstract void receiveMessage(Message message);

	/**
	 * Register the socket, useful for some implementations.
	 * @param URL is the URL where the socket is to be registered.
	 */
	public abstract void register(String URL);

	/** Unregister the socket. */
	public abstract void unRegister();

	/**
	 * Set the handler, which receives the message.
	 * Usually, these messages are passed onto a SynchronizedSocket instance.
	 * @param imrh is the instance which implements the IMessageReceivedHandler interface.
	 */
	public void addMessageReceivedHandler(IMessageReceivedHandler imrh) {
		receivedHandlers.add(imrh);
	}

	/** @return the URL of the socket. */
	public abstract String getURL();

	/**
	 * Called when a message is received, to actually deliver the message to the handler.
	 * @param message is the message, which is to be delivered.
	 */
	protected void notifyReceivedHandlers(Message message, IMessageReceivedHandler caller) {
		if (receivedHandlers.size() > 0)
			for (IMessageReceivedHandler imrh : receivedHandlers)
				if (imrh != caller)
					imrh.onMessageReceived(message);
	}
}
