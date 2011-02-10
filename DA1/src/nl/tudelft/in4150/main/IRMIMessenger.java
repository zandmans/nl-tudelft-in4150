package nl.tudelft.in4150.main;

import nl.tudelft.in4150.objects.Message;

import java.rmi.Remote;

public interface IRMIMessenger extends Remote {
	public void receiveMessage(Message message) throws java.rmi.RemoteException;
}
