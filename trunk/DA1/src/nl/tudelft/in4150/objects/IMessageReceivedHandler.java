/**
 * Software written for the Distributed Systems Lab course.
 * 
 * @author H. Pijper
 * @author P.A.M. Anemaet
 * @author N. Brouwers
 */
package nl.tudelft.in4150.objects;

public interface IMessageReceivedHandler {
	/**
	 * Called when a message has been received by 
	 * the underlying socket.
	 * 
	 * @param message is the message received.
	 */
	public void onMessageReceived(Message message);
}
