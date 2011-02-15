package nl.tudelft.in4150.main;

import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

public abstract class RMIClient extends UnicastRemoteObject implements IReceiver {
	public int clientID;
	public String clientURL;

	public RMIClient(int clientID) throws java.rmi.RemoteException {
		if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());

		this.clientID = clientID;
		this.clientURL = createURL(this.clientID);

		try {
			java.rmi.Naming.bind(this.clientURL, this); }
		catch (Exception e) { e.printStackTrace(); }
	}

	// Destructor. Contains cleanup.
	public void finalize() throws Throwable {
		super.finalize();
		try {
			java.rmi.Naming.unbind(this.clientURL); }
		catch (Exception e) { e.printStackTrace(); }
	}

	// Returns a RMI client URL based on its client integer ID. Hardcoded within this demo, but could have been done using the registry.
	public static String createURL(int clientID) {
		return("rmi://localhost/in4150_" + clientID);
	}

	// Sends a message object to client with integer ID receiverID.
	public void sendMessage(Message msg, int receiverID) {
		// We could directly call receiveMessage through RMI here, but if we extend it with random delays, the whole thread is delayed (blocking).
		new RandomDelayedTransfer(msg, receiverID);
	}

	// RMI callable function (see IReceiver) to 'receive' a message object.
	public void receiveMessage(Message msg) throws java.rmi.RemoteException {
		this.onMessageReceived(msg);
	}

	// Initializes the RMI registry.
	public static void initializeRMI(int port) {
		try { java.rmi.registry.LocateRegistry.createRegistry(port); }
		catch (Exception e) { throw new Error(e); }
	}

	// Should be implemented by subclassing clients; this function is called when a message is received.
	public abstract void onMessageReceived(Message msg);
}
