package nl.tudelft.in4150.main;

import java.rmi.Remote;

// This interface specifies the RMI callable functions (within a RMIClient)
public interface IReceiver extends Remote {
	public void receiveMessage(Message msg) throws java.rmi.RemoteException;
}
