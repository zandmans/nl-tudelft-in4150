/**
 * @author N. de Jong
 */

package nl.tudelft.in4150.objects;

import java.rmi.Remote;

public interface IRMIMessenger extends Remote {
	public void receiveMessage(Message message) throws java.rmi.RemoteException;
}
