/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex2;

import java.util.*;

public class TestClient extends RMIClient {
	private boolean running; /* Running state of the text client; assigning false (while running) will cause clean termination of the thread. */
	private int id; // own id
	private int nLeft; // neighbour left
	private int nRight; // neighbour right
	private boolean elected=false;
	private int ntid=0, nntid=0;

	/** Create a new client */
	public TestClient(int clientID) throws java.rmi.RemoteException {
		super(clientID);

		this.id = Config.CLIENT_ID[this.clientID]; // retrieve own id

		this.nLeft = this.clientID == 0 ? this.clientID-1 : Config.CLIENT_COUNT-1; // define neighbour left
		this.nRight = this.clientID == Config.CLIENT_COUNT-1 ? 0 : this.clientID+1; // define neighbour right

		if(this.clientID == 0) this.sendMessage(new Message(this.id, 1), this.nRight); // send message to right neighbour

		synchronized (this) {
			Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, clientID); // Let all others know this new client exists. (Simple version)
		}
	}

	/** Show data when it is received. */
	public void onMessageReceived(Message message) {

		if(this.elected) {
			System.out.println("CLIENT " + this.clientID + " shouts: " + this.id);
			this.sendMessage(new Message(this.id, 1), this.nRight);
			return;
		}

		if(message.type==1) {
			System.out.println("(==>) SND1: " + this.id + " FROM " + this.clientID);
			if((this.ntid=message.id)==this.id) this.elected = true;
			System.out.println("(<==) RCV1: (" + this.ntid + ") OWN ID: " + this.id + " BY " + this.clientID);
			this.sendMessage(new Message(Math.max(this.id, message.id), 2), this.nRight);
			System.out.println("(==>) SND2: " + Math.max(this.id, message.id) + " FROM " + this.clientID);
		} else if(message.type==2) {
			if((this.nntid=message.id)==this.id) this.elected = true;
			System.out.println("(<==) RCV2: (" + this.ntid + "," + this.nntid + ") OWN ID: " + this.id + " BY " + this.clientID);
			if((this.ntid >= this.id) && (this.ntid >= this.nntid)) {
				System.out.println("(+++) CHANGE ID " + this.id + " TO " + this.ntid);
				this.id = this.ntid;
			}
			this.sendMessage(new Message(this.id, 1), this.nRight);
		}
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		initializeRMI(Config.REGISTRY_PORT);

		try { for (int i = 0; i < Config.CLIENT_COUNT; ++i) new TestClient(i); }
		catch (Exception e) { e.printStackTrace(); }
	}

}
