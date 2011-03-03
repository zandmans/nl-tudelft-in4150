/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex2;

enum STATE {
	ACTIVE_1,	// Waiting for first message receival
	ACTIVE_2, // Waiting for second message receival
	RELAY,		// In relay state
	SHUT_UP
}

public class Component extends RMIClient {
	private STATE state;

	private int nRight; // neighbour right

	private int id; // own id
	private int tid; // elected id
	private int ntid=0;
	private boolean elected=false;

	/** Create a new client */
	public Component(int clientID) throws java.rmi.RemoteException {
		super(clientID);

		this.tid = (this.id = Config.CLIENT_ID[this.clientID]); // retrieve own id
		this.nRight = this.clientID == Config.CLIENT_COUNT-1 ? 0 : this.clientID+1; // define neighbour right

		this.sendMessage(new Message(this.tid), this.nRight); // send message to right neighbour

		synchronized (this) {
			this.state = STATE.ACTIVE_1;
			Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, clientID); // Let all others know this new client exists. (Simple version)
		}
	}

	/** Show data when it is received. */
	public synchronized void onMessageReceived(Message message) {
		// Code for 'termination'.
		if (this.state == STATE.SHUT_UP) return;
		if (message.elected) {
			System.out.println("I ("+this.id+") know you are elected. I will shut up now!");
			this.sendMessage(new Message(0, true), this.nRight);
			this.state = STATE.SHUT_UP;
			return;
		}

		// Actual implementation.
		System.out.print("ID="+this.id+" ; TID="+this.tid+" ; NTID="+this.ntid+" ; NNTID="+message.id+" ; STATE="+this.state+" ; ");
		switch (this.state) {
			case ACTIVE_1: this.processFirstMessage(message); break;
			case ACTIVE_2: this.processSecondMessage(message); break;
			case RELAY:    this.relay(message); break;
		}
		if (!this.elected) System.out.println("NEW STATE="+this.state);
	}

	/** Receive ntid and process it */
	public void processFirstMessage(Message message) {
		if((this.ntid=message.id)==this.id) this.electMe(); // if ntid = id then elected <- true

		this.sendMessage(new Message(Math.max(this.tid, this.ntid), message.elected || this.elected), this.nRight); // send max(tid,ntid)
		if (!this.elected) System.out.print("SENT="+Math.max(this.tid, this.ntid)+" ; ");

		this.state = STATE.ACTIVE_2;
	}

	/** Receive nntid and process it */
	public void processSecondMessage(Message message) {
		if(message.id==this.id) this.electMe();	// if nntid = id then elected <- true // nntid === message.id

		if((this.ntid >= this.tid) && (this.ntid >= message.id)) { //if ntid >= tid and ntid >= nntid
			this.tid = this.ntid;	// then tid <- ntid

			this.sendMessage(new Message(this.tid, message.elected || this.elected), this.nRight);
			if (!this.elected) System.out.print("SENT="+this.tid+" ; ");

			this.state = STATE.ACTIVE_1;
		} else this.state = STATE.RELAY;
	}

	/** The relay processes */
	public void relay(Message message) {
		if(message.id == this.id) this.electMe();
		this.sendMessage(new Message(message.id, this.elected), this.nRight);
		if (!this.elected) System.out.print("SENT="+message.id+" ; ");
	}

	public void electMe() {
		this.elected = true;
		System.out.println("\n\nClient "+this.id+" shouts: I'm elected!");
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		initializeRMI(Config.REGISTRY_PORT);

		try { for (int i = 0; i < Config.CLIENT_COUNT; ++i) new Component(i); }
		catch (Exception e) { e.printStackTrace(); }
	}

}
