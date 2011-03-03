/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex2;

enum STATE {
	ACTIVE_1,	// Waiting for first message receival
	ACTIVE_2, // Waiting for second message receival
	RELAY			// In relay state
}

public class Component extends RMIClient {
	private boolean running; /* Running state of the text client; assigning false (while running) will cause clean termination of the thread. */
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
		System.out.print("ID="+this.id+" ; TID="+this.tid+" ; NTID="+this.ntid+" ; NNTID="+message.id+" ; STATE="+this.state+" ; ");
		switch (this.state) {
			case ACTIVE_1: this.processFirstMessage(message); break;
			case ACTIVE_2: this.processSecondMessage(message); break;
			case RELAY:    this.relay(message); break;
		}
		System.out.println("NEW STATE="+this.state);
	}

	/** Receive ntid and process it */
	public void processFirstMessage(Message message) {
		if((this.ntid=message.id)==this.id) this.elected = true; // if ntid = id then elected <- true
		//System.out.println("(<==) RCV1: (" + this.ntid + ") OWN ID: " + this.id + " BY " + this.clientID);

		this.sendMessage(new Message(Math.max(this.tid, this.ntid)), this.nRight); // send max(tid,ntid)
		//System.out.println("(==>) SND2: " + Math.max(this.tid, message.id) + " FROM " + this.clientID);

		this.state = STATE.ACTIVE_2;
	}

	/** Receive nntid and process it */
	public void processSecondMessage(Message message) {
		if(message.id==this.id) this.elected = true;	// if nntid = id then elected <- true // nntid === message.id
		//System.out.println("(<==) RCV2: (" + this.ntid + "," + message.id + ") OWN ID: " + this.id + " BY " + this.clientID);

		if((this.ntid >= this.tid) && (this.ntid >= message.id)) { //if ntid >= tid and ntid >= nntid
			//System.out.println("(+++) CHANGE TID " + this.tid + " TO " + this.ntid);
			this.tid = this.ntid;	// then tid <- ntid

			//System.out.println("(==>) SND1: " + this.id + " FROM " + this.clientID);
			this.sendMessage(new Message(this.tid), this.nRight);

			this.state = STATE.ACTIVE_1;
		} else this.state = STATE.RELAY;
	}

	/** The relay processes */
	public void relay(Message message) {
		if(message.id == this.id) {
			this.elected = true;
			System.out.println("Client "+this.id+" shouts: I'm elected!");
		}
		this.sendMessage(new Message(this.id), this.nRight);
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		initializeRMI(Config.REGISTRY_PORT);

		try { for (int i = 0; i < Config.CLIENT_COUNT; ++i) new Component(i); }
		catch (Exception e) { e.printStackTrace(); }
	}

}
