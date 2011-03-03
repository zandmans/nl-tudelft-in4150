/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex2;

public class Component extends RMIClient {
	private boolean running; /* Running state of the text client; assigning false (while running) will cause clean termination of the thread. */
	private int id; // own id
	private int tid; // elected id
	private int nRight; // neighbour right
	private boolean elected=false;
	private int ntid=0, nntid=0;
	private boolean relay = false;

	/** Create a new client */
	public Component(int clientID) throws java.rmi.RemoteException {
		super(clientID);

		this.id = Config.CLIENT_ID[this.clientID]; // retrieve own id
		this.nRight = this.clientID == Config.CLIENT_COUNT-1 ? 0 : this.clientID+1; // define neighbour right

		if(this.clientID == 0) this.sendMessage(new Message(this.id, 1), this.nRight); // send message to right neighbour

		synchronized (this) {
			Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, clientID); // Let all others know this new client exists. (Simple version)
		}
	}

	/** Show data when it is received. */
	public void onMessageReceived(Message message) {
		this.tid = this.id;

		if(this.relay) {
			this.relay(message);
			return;
		}

		this.sendMessage(new Message(this.tid, 1), this.nRight);
		if(message.type==1) this.processFirstMessage(message);
		else if(message.type==2) this.processSecondMessage(message);
	}

	/** Receive ntid and process it */
	public void processFirstMessage(Message message) {
		System.out.println("(==>) SND1: " + this.id + " FROM " + this.clientID);

		if((this.ntid=message.id)==this.id) this.elected = true; // if ntid = id then elected <- true

		System.out.println("(<==) RCV1: (" + this.ntid + ") OWN ID: " + this.id + " BY " + this.clientID);

		this.sendMessage(new Message(Math.max(this.tid, this.ntid), 2), this.nRight); // send max(tid,ntid)

		System.out.println("(==>) SND2: " + Math.max(this.id, message.id) + " FROM " + this.clientID);
	}

	/** Receive nntid and process it */
	public void processSecondMessage(Message message) {
		if((this.nntid=message.id)==this.id) this.elected = true;	// if nntid = id then elected <- true

		System.out.println("(<==) RCV2: (" + this.ntid + "," + this.nntid + ") OWN ID: " + this.id + " BY " + this.clientID);

		if((this.ntid >= this.tid) && (this.ntid >= this.nntid)) { //if ntid >= tid and ntid >= nntid
			System.out.println("(+++) CHANGE ID " + this.id + " TO " + this.ntid);

			this.tid = this.ntid;	// then tid <- ntid
		}
		else {
			this.relay = true;
			this.relay(message);
		}
	}

	/** The relay processes */
	public void relay(Message message)
	{
		if(message.id == this.id) this.elected = true;
		this.sendMessage(new Message(this.id, 1), this.nRight);
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		initializeRMI(Config.REGISTRY_PORT);

		try { for (int i = 0; i < Config.CLIENT_COUNT; ++i) new Component(i); }
		catch (Exception e) { e.printStackTrace(); }
	}

}
