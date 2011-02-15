package nl.tudelft.in4150.main;

/* This class extends messages with random delays (simulates transfer time by wire or WiFi). This class
   creates a new temporary thread, sleeps for a random amount of milliseconds and then actually delivers the
   message object to the receiver. */
public class RandomDelayedTransfer implements Runnable {
	private Message msg;
	private int receiverID;

	public RandomDelayedTransfer(Message msg, int receiverID) {
		this.msg = msg;
		this.receiverID = receiverID;
		new Thread(this).start();
	}

	public void run() {
		try { Thread.sleep((int)(Math.random() * 1000)); } // Wait for some random delay
		catch (InterruptedException e) { e.printStackTrace(); }

		try {
			IReceiver receiver = (IReceiver) java.rmi.Naming.lookup(RMIClient.createURL(receiverID));
			receiver.receiveMessage(msg);
		} catch (Exception e) { e.printStackTrace(); }
	}
}
