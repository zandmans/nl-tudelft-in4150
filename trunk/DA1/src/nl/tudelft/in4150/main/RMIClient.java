package nl.tudelft.in4150.main;

import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

public abstract class RMIClient extends UnicastRemoteObject implements IReceiver {
	public int clientID;
	public String clientURL;

	// TestClient specific part
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

	public static String createURL(int clientID) {
		return("rmi://localhost/in4150_" + clientID);
	}

	public void sendMessage(Message msg, int receiverID) {
		try {
			IReceiver receiver = (IReceiver) java.rmi.Naming.lookup(createURL(receiverID));
			receiver.receiveMessage(msg);
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void receiveMessage(Message msg) throws java.rmi.RemoteException {
		// TODO: Create some random delayal function (travel time).
		this.onMessageReceived(msg);
	}

	public static void initializeRMI(int port) {
		try { java.rmi.registry.LocateRegistry.createRegistry(port); }
		catch (Exception e) { throw new Error(e); }
	}

	public abstract void onMessageReceived(Message msg);
}
