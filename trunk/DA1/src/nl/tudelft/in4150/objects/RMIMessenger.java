/**
 * @author N. de Jong
 */
package nl.tudelft.in4150.objects;

import java.rmi.server.UnicastRemoteObject;

public class RMIMessenger extends UnicastRemoteObject implements IRMIMessenger {
	private RMISocket parent;

	private static final long serialVersionUID = 1234567890324524L;

	public RMIMessenger(RMISocket parent) throws Exception {
		this.parent = parent;
	}

	public void receiveMessage(Message message) throws java.rmi.RemoteException {
		parent.receiveMessage(message);
	}
}
