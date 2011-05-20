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
package nl.tudelft.in4150.ex3;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

/** This is the actual RMIClient; which takes care of the transfer of messages by means of RMI. */
public abstract class RMIClient extends UnicastRemoteObject implements IReceiver {
	private java.rmi.registry.Registry registry;

	public int clientID;
	public String clientName;

	public int[] clients;

	public RMIClient(int clientID, String registryServer, int[] clients) throws java.rmi.RemoteException {
		if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());

		this.clientID = clientID;
		this.clientName = createName(this.clientID);
		this.clients = clients; // Handig om te weten wie er nog meer bestaan, maar niet strikt nodig.

		try {
			this.registry = java.rmi.registry.LocateRegistry.getRegistry(registryServer);
			this.registry.rebind(this.clientName, this /*UnicastRemoteObject.exportObject(this, 0)*/); // Moet hier address voor name?!
		} catch (Exception e) { e.printStackTrace(); }

	}

	/** Destructor. Contains cleanup. */
	public void finalize() throws Throwable {
		super.finalize();
		try { this.registry.unbind(this.clientName); }
		catch (Exception e) { e.printStackTrace(); }
	}

	/** Returns a RMI client name for the registry, based on a clientID */
	public static String createName(int clientID) {
		return("in4150-Ex3-"+clientID);
	}

	/** Sends a message object to client with integer ID receiverID. */
	public void sendMessage(Message msg, int receiverID) {
		//Config.SENT_MESSAGES++;
		try { new RandomDelayedTransfer(msg, (IReceiver) this.registry.lookup(createName(receiverID))); } // Asynchronous
		//try { ((IReceiver)this.registry.lookup(createName(receiverID))).receiveMessage(msg); } // Synchronous
		catch (Exception e) { e.printStackTrace(); }
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
	private IReceiver receiver;

	public RandomDelayedTransfer(Message msg, IReceiver receiver) {
		this.msg = msg;
		this.receiver = receiver;
		new Thread(this).start();
	}

	public void run() {
		try { Thread.sleep((int) (Config.MIN_DELAY + Math.random() * (Config.MAX_DELAY - Config.MIN_DELAY))); } // Wait for some random delay
		catch (InterruptedException e) { e.printStackTrace(); }

		try { receiver.receiveMessage(msg); }
		catch (Exception e) { e.printStackTrace(); }
	}
}
