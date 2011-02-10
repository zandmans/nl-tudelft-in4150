package nl.tudelft.in4150.main;

import java.rmi.*;

import nl.tudelft.in4150.objects.*;

public interface IRMIMessenger extends Remote {
	public void receiveMessage(Message message) throws java.rmi.RemoteException;
}
