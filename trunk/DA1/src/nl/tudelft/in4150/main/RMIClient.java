/**
 * @author N. de Jong
 *
 * This file contains 3 separate objects:
 *   > class			RMIClient								The actual RMI client
 *   > interface	IReceiver								Supportive to RMIClient; Specifies RMI callable functions.
 *   > class			RandomDelayedTransfer		Supportive to RMIClient; Implements random delayed message (non-blocking).
 *
 * Together, these form the (RMI-based) simulated transport layer for this project.
 *
 */
package nl.tudelft.in4150.ex1;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

/** This is the actual RMIClient; which takes care of the transfer of messages by means of RMI. */
public abstract class RMIClient extends UnicastRemoteObject implements IReceiver {
	public int clientID;
	public String clientURL;

	public RMIClient(int clientID) throws java.rmi.RemoteException {
		if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());

		this.clientID = clientID;
		this.clientURL = createURL(this.clientID);

		try { java.rmi.Naming.bind(this.clientURL, this); }
		catch (Exception e) { e.printStackTrace(); }
	}

	/** Destructor. Contains cleanup. */
	public void finalize() throws Throwable {
		super.finalize();
		try { java.rmi.Naming.unbind(this.clientURL); }
		catch (Exception e) { e.printStackTrace(); }
	}

	/** Returns a RMI client URL based on its client integer ID. Hardcoded within this demo, but could have been done using the registry. */
	public static String createURL(int clientID) {
		return ("rmi://localhost/in4150_" + clientID);
	}

	/** Sends a message object to client with integer ID receiverID. */
	public void sendMessage(Message msg, int receiverID) {
		/** We could directly call receiveMessage through RMI here, but if we extend it with random delays, the whole thread is delayed (blocking). */
		new RandomDelayedTransfer(msg, receiverID);
	}

	/** RMI callable function (see IReceiver) to 'receive' a message object. */
	public void receiveMessage(Message msg) throws java.rmi.RemoteException {
		this.onMessageReceived(msg);
	}

	/** Initializes the RMI registry. */
	public static void initializeRMI(int port) {
		try { java.rmi.registry.LocateRegistry.createRegistry(port); }
		catch (Exception e) { throw new Error(e); }
	}

	/** Should be implemented by subclassing clients; this function is called when a message is received. */
	public abstract void onMessageReceived(Message msg);
}

/** This interface specifies the RMI callable functions (within a RMIClient) */
interface IReceiver extends Remote {
	public void receiveMessage(Message msg) throws java.rmi.RemoteException;
}

/** This class extends messages with random delays (simulates transfer time by wire or WiFi). This class
    creates a new temporary thread, sleeps for a random amount of milliseconds and then actually delivers the
    message object to the receiver. */
class RandomDelayedTransfer implements Runnable {
	private Message msg;
	private int receiverID;

	public RandomDelayedTransfer(Message msg, int receiverID) {
		this.msg = msg;
		this.receiverID = receiverID;
		new Thread(this).start();
	}

	public void run() {
		try { Thread.sleep((int) (Config.MIN_DELAY + Math.random() * (Config.MAX_DELAY - Config.MIN_DELAY))); } // Wait for some random delay
		catch (InterruptedException e) { e.printStackTrace(); }

		try {
			IReceiver receiver = (IReceiver) java.rmi.Naming.lookup(RMIClient.createURL(receiverID));
			receiver.receiveMessage(msg);
		} catch (Exception e) { e.printStackTrace(); }
	}
}