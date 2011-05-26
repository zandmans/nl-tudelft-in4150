/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;
import java.util.LinkedList;
import java.util.ArrayList;

public class Component extends RMIClient implements Runnable {
	private class threadHandleMissingMessages implements Runnable {
		Component caller;
		public threadHandleMissingMessages(Component caller) { this.caller = caller; }
		public void run() {
			for (LinkedList<Integer> missingMessage : this.caller.getMissingMessages()) {
				if (Config.OUTPUT_MISSING_MESSAGES) System.out.println("I ("+this.caller.clientID+") did not receive message "+FormatMessagePath(missingMessage));
				this.caller.HandleSubMsg(missingMessage, Config.DEFAULT_VALUE);
			}
		}
	}

	private class threadSendMessages implements Runnable {
		Component caller;
		Message roundMessage;
		public threadSendMessages(Component caller) {
			this.caller = caller;
			if (caller.roundMessage != null)
				this.roundMessage = caller.roundMessage.copy();
		}
		public void run() {
			if (this.roundMessage != null) // Do not send messages in round 0 if we aren't the commander
				for (int i=1; i<caller.clients.length; i++) {
					if (caller.faultLevel > 0) // Try to corrupt messages
						for (int j=0; j<this.roundMessage.values.size(); j++)
							this.roundMessage.values.set(j, (Math.random() > 0.5 ? 1 : 0));
							//this.roundMessage.values.set(j, 0);
					if ((caller.faultLevel < 2) || (Math.random() > 0.5)) { // Try to drop messages
						caller.sendMessage(roundMessage.copy(), i);
						Config.SENT_MESSAGES[caller.clientID] += roundMessage.values.size();
					}
				}
		}
	}

	// Properties
	private int maxFaults;		// Algorithm property.
	private int faultLevel;		// How Byzantine am I? ;) 0=Loyal, 1=Corrupting, 2=Corrupting and dropping messages
	private int initSleep;		// Initial time to sleep before we start to execute. Used to wait for all clients to register to RMI.

	protected DecisionObject data;

	// Vary per round
	private Message roundMessage;
	private int currentRound;
	private ArrayList<LinkedList<Integer>> expectedCurrent;		// Messages we expect to receive in the current round. Elements are removed upon receiving an expected message.
	private ArrayList<LinkedList<Integer>> expectedNextRound;	// Prepare messages we expect in the next round. Elements are added upon receiving a prior message to be extended.

	/** Create a new client */
	public Component(int clientID, String registryServer, int[] clients, int maxFaults, int faultLevel, int initSleep) throws java.rmi.RemoteException {
		super(clientID, registryServer, clients);

		//this.data = null; // Tree variant
		this.data = new DecisionMap(maxFaults, clients.length);
		this.faultLevel = faultLevel;
		this.initSleep = initSleep;
		this.currentRound = 0;
		this.maxFaults = maxFaults;

		expectedCurrent = new ArrayList<LinkedList<Integer>>();
		expectedNextRound = new ArrayList<LinkedList<Integer>>();
	}

	// Helper functions
	public LinkedList<Integer> CreateNewRootPath() { LinkedList<Integer> path = new LinkedList<Integer>(); path.add(0); return(path); }
	public String FormatMessagePath(LinkedList<Integer> path) { LinkedList<Integer> pathCopy = (LinkedList<Integer>)path.clone(); String out = "["+pathCopy.removeFirst(); while (!pathCopy.isEmpty()) out += "-"+pathCopy.removeFirst(); return(out+"]"); }

	private synchronized void removeExpectedCurrent(LinkedList<Integer> toRemove) { this.expectedCurrent.remove(toRemove); }
	private synchronized void addExpectedNextRound (LinkedList<Integer> toAdd   ) { this.expectedNextRound.add(toAdd);     }
	private synchronized ArrayList<LinkedList<Integer>> getMissingMessages () { return((ArrayList<LinkedList<Integer>>)this.expectedCurrent.clone()); }
	private synchronized void nextExpectedRound() { this.expectedCurrent = this.expectedNextRound; this.expectedNextRound = new ArrayList<LinkedList<Integer>>(); }

	public void run() {
		try { Thread.sleep(this.initSleep); } // Wait for everyones initialization.
		catch (InterruptedException e) { e.printStackTrace(); }

		//System.out.println("I started at "+System.currentTimeMillis());

		if (this.clientID == 0) { // Commander stuff
			LinkedList<Integer> path = CreateNewRootPath();
			int value = 1; Message msg;
			for (int i=1; i<this.clients.length; i++) {
				if (this.faultLevel > 0) value = (Math.random() > 0.5 ? 1 : 0);
				msg = new Message(0, 0); msg.AddSubMsg(path, value);
				if ((this.faultLevel < 2) || (Math.random() > 0.5)) {
					this.sendMessage(msg, i);
					Config.SENT_MESSAGES[this.clientID] += msg.values.size();
				}
			}
			//data = new DecisionTree(this.clientID, value); // Overbodig.
			if (this.faultLevel == 0) System.out.println("I (the commander) told everyone to decide on " + value);
			else 											System.out.println("I (the commander) am a faulty process so my decision does not matter =)");
		} else { // Lieutenant stuff
			// Messages we expect in round 0; intialization.
			this.addExpectedNextRound(CreateNewRootPath());
			this.nextExpectedRound();

			for (int round = 0; round<=this.maxFaults; round++) {
				this.currentRound = round;
				if (this.clientID == 1) System.out.println("Executing round "+round+"...");

				// Send messages.
				(new Thread(new threadSendMessages(this))).start();
				roundMessage = new Message(round+1, this.clientID); // Reset message object for next round.

				// Sleep for 80% of round time
				try { Thread.sleep(Config.ROUND_TIME_A[round]); }
				catch (InterruptedException e) { e.printStackTrace(); }

				// Handle non-received messages
				(new Thread(new threadHandleMissingMessages(this))).start();

				// Sleep for 20%/2 of round time
				try { Thread.sleep(Config.ROUND_TIME_B[round]); }
				catch (InterruptedException e) { e.printStackTrace(); }

				this.nextExpectedRound();
			}

			// Decide output
			this.currentRound++; // So all possibly out-of-date messages are regarded as out-of-date =)
			if (this.faultLevel == 0) System.out.println("I ("+this.clientID+") have decided on " + this.data.Decide());
			else 											System.out.println("I ("+this.clientID+") am a faulty process so my decision does not matter =)");

			//System.out.println(this.data);
		}

		if(Config.OUTPUT_MESSAGE_COUNTS_INDIV) System.out.println(" I ("+this.clientID+")have sent " + Config.SENT_MESSAGES[this.clientID] + " and received "+Config.RECEIVED_MESSAGES[this.clientID]+" messages.");
		if(Config.OUTPUT_MESSAGE_COUNTS_SUM && this.clientID == 1) {
			int s=0, r=0;
			for (int i = 0; i<this.clients.length; i++) {
				s += Config.SENT_MESSAGES[i];
				r += Config.RECEIVED_MESSAGES[i];
			} System.out.println("Total sent: "+s+" and received: "+r);
		}
	}



	public synchronized void HandleSubMsg(LinkedList<Integer> path, int value) {
		LinkedList<Integer> newPath;

		// Add information to DecisionTree
		//if (path.size() == 1) this.data = new DecisionTree(path.get(0), value);
		//else this.data.AddNewNode(path, value);
		this.data.AddNewNode(path, value);

		// If we have another round to go, determine expected messages based on this one, and additionally add our own message to roundMessage object.
		if (this.currentRound < this.maxFaults) {
			for (int i=1; i<this.clients.length; i++) {
				if (!path.contains(i)) {
					newPath = new LinkedList<Integer>(path);
					newPath.add(i);
					this.addExpectedNextRound((LinkedList<Integer>)newPath.clone());
					if (this.clientID == i) roundMessage.AddSubMsg(newPath, value);
				}
			}
		}
	}

	/** Code executed by the lieutenant */
	public synchronized void onMessageReceived(Message msg) {
		LinkedList<Integer> path;
		if (this.clientID > 0) { // Somehow, we receive a message as commander, which is not send from this application..
			if (msg.currentRound == this.currentRound) { // Check round = valid
				if (Config.OUTPUT_RECEIVED_MESSAGES) System.out.println("I ("+this.clientID+") have received round "+this.currentRound+" message from "+msg.sender+"; "+msg.toString());

				Config.RECEIVED_MESSAGES[this.clientID] += msg.values.size();
				for (int i=0; i<msg.values.size(); i++) { // Handle all sub-messages packed in roundMessage object.
					path = msg.paths.get(i);
					this.removeExpectedCurrent(path); // Remove message from expected messages because we did actually receive it :)
					this.HandleSubMsg(path, msg.values.get(i));
				}

			} else if (Config.OUTPUT_OUTOFDATE_MESSAGES) System.out.println("I ("+this.clientID+") received OUT-OF-DATE (Now="+this.currentRound+",Msg="+msg.currentRound+") message from "+msg.sender+"; ignoring.");
		}
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		if (args.length > 0) { // Multi process version. 1st param: my ID. 2nd param: registry server. 3rd param: client count. 4th param: max_faults. 5th param: fault_level. 6th param: sleep interval at start. 7th param: am I registry?
			try {
				if (args[6].equals("true")) initializeRMI(Config.REGISTRY_PORT);

				int[] clients = new int[Integer.parseInt(args[2])]; for (int i=0; i<clients.length; i++) clients[i] = i;
				Component me = new Component(Integer.parseInt(args[0]), args[1], clients, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
				new Thread(me).start();
			} catch (Exception e) { e.printStackTrace(); }
		} else { // Standard configuration in 1 process.
			initializeRMI(Config.REGISTRY_PORT);

			Component comps[] = new Component[Config.CLIENT_ID.length];

			try {
				for (int i = 0; i < Config.CLIENT_ID.length; ++i)
					comps[i] = new Component(Config.CLIENT_ID[i], "localhost", Config.CLIENT_ID, Config.FAULTS, Config.FL[i], 1000);
				for (int i = 0; i < Config.CLIENT_ID.length; ++i)
					new Thread(comps[i]).start();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}
