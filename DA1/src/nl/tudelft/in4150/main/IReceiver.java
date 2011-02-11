package nl.tudelft.in4150.main;

import java.rmi.Remote;

public interface IReceiver extends Remote {
	public void receiveMessage(Message msg) throws java.rmi.RemoteException;
}
