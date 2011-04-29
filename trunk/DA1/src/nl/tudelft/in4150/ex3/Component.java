/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

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
	public Component(int clientID, int rightID, String registryServer, int[] clients) throws java.rmi.RemoteException {
		super(clientID, registryServer, clients);

		this.tid = (this.id = clientID); // retrieve own id
		this.nRight = rightID; // define neighbour right

		synchronized (this) {
			this.state = STATE.ACTIVE_1;
			//Config.CLIENT_INIT = Math.max(Config.CLIENT_INIT, clientID); // Let all others know this new client exists. (Simple version)
		}
	}

	public void initiate() {
		this.sendMessage(new Message(this.tid), this.nRight); // send message to right neighbour
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
		if (args.length > 0) { // Multi process version. 1st param: my ID. 2nd param: neighbour ID. 3rd param: registry server. 4th param: sleep interval at start. 5th param: do I initiate? 6th param: am I registry?
			try {
				if (args[5].equals("true")) initializeRMI(Config.REGISTRY_PORT);

				Component me = new Component(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], null);

				try { Thread.sleep(Integer.parseInt(args[3])); } // Wait for everyones initialization.
				catch (InterruptedException e) { e.printStackTrace(); }

				if (args[4].equals("true")) me.initiate();
			} catch (Exception e) { e.printStackTrace(); }
			
		} else { // Standard configuration in 1 process.
			initializeRMI(Config.REGISTRY_PORT);

			Component comps[] = new Component[Config.CLIENT_COUNT];

			try {
				for (int i = 0; i < Config.CLIENT_COUNT; ++i)
					comps[i] = new Component(Config.CLIENT_ID[i], Config.CLIENT_ID[i == Config.CLIENT_COUNT-1 ? 0 : i+1], "localhost", Config.CLIENT_ID);
			} catch (Exception e) { e.printStackTrace(); }

			try { Thread.sleep(1000); } // Wait for everyones initialization.
			catch (InterruptedException e) { e.printStackTrace(); }

			for (int i = 0; i < Config.CLIENT_COUNT; ++i)
				comps[i].initiate(); // Everyone initiates
		}
	}
}
