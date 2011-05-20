/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;
import java.util.LinkedList;
import java.util.ArrayList;

public class Component extends RMIClient implements Runnable {
	// Properties
	private int maxFaults;
	private int faultLevel;
	private int initSleep;

	private DecisionTree data;

	// Vary per round
	private Message roundMessage;
	private int currentRound;
	private ArrayList<LinkedList<Integer>> expectedCurrent;		// Messages we expect to receive in the current round. Elements are removed upon receiving an expected message.
	private ArrayList<LinkedList<Integer>> expectedNextRound;	// Prepare messages we expect in the next round. Elements are added upon receiving a prior message to be extended.

	/** Create a new client */
	public Component(int clientID, String registryServer, int[] clients, int maxFaults, int faultLevel, int initSleep) throws java.rmi.RemoteException {
		super(clientID, registryServer, clients);

		this.data = null;
		this.faultLevel = faultLevel;
		this.initSleep = initSleep;
		this.currentRound = 0;
		this.maxFaults = maxFaults;

		expectedCurrent = new ArrayList<LinkedList<Integer>>();
		expectedNextRound = new ArrayList<LinkedList<Integer>>();

		new Thread(this).start();
	}

	public LinkedList<Integer> CreateNewRootPath() { LinkedList<Integer> path = new LinkedList<Integer>(); path.add(0); return(path); }
	public String FormatMessagePath(LinkedList<Integer> path) { LinkedList<Integer> pathCopy = (LinkedList<Integer>)path.clone(); String out = "["+pathCopy.removeFirst(); while (!pathCopy.isEmpty()) out += "-"+pathCopy.removeFirst(); return(out+"]"); }

	private synchronized void removeExpectedCurrent(LinkedList<Integer> toRemove) { this.expectedCurrent.remove(toRemove); }
	private synchronized void addExpectedNextRound (LinkedList<Integer> toAdd   ) { this.expectedNextRound.add(toAdd);     }
	private synchronized ArrayList<LinkedList<Integer>> getMissingMessages () { return((ArrayList<LinkedList<Integer>>)this.expectedCurrent.clone()); }
	private synchronized void nextExpectedRound() { this.expectedCurrent = this.expectedNextRound; this.expectedNextRound = new ArrayList<LinkedList<Integer>>(); }

	public void run() {
		ArrayList<LinkedList<Integer>> missing;

		try { Thread.sleep(this.initSleep); } // Wait for everyones initialization.
		catch (InterruptedException e) { e.printStackTrace(); }

		if (this.clientID == 0) { // Commander stuff
			LinkedList<Integer> path = CreateNewRootPath();
			int value = 1; Message msg;
			for (int i=1; i<this.clients.length; i++) {
				if (this.faultLevel > 0) value = (Math.random() > 0.5 ? 1 : 0);
				msg = new Message(0, 0); msg.AddSubMsg(path, value);
				if ((this.faultLevel < 2) || (Math.random() > 0.5)) this.sendMessage(msg, i);
			}
			data = new DecisionTree(this.clientID, value);
		} else { // Lieutenant stuff
			// Messages we expect in round 0; intialization.
			this.addExpectedNextRound(CreateNewRootPath());
			this.nextExpectedRound();

			for (int round = 0; round<=this.maxFaults; round++) {
				this.currentRound = round;
				System.out.println("I ("+this.clientID+") am executing round "+round+"...");

				// Send messages. Do not send messages in round 0 if we aren't the commander
				if (this.roundMessage != null)
					for (int i=1; i<this.clients.length; i++) {
						if (this.faultLevel > 0) // Try to corrupt messages
							for (int j=0; j<this.roundMessage.values.size(); j++)
								this.roundMessage.values.set(j, (Math.random() > 0.5 ? 1 : 0));
								//this.roundMessage.values.set(j, 0);
						if ((this.faultLevel < 2) || (Math.random() > 0.5)) // Try to drop messages
							this.sendMessage(roundMessage.copy(), i);
					}

				// Reset message object for next round.
				roundMessage = new Message(round+1, this.clientID);

				// Sleep for round time
				try { Thread.sleep(Config.ROUND_TIME); }
				catch (InterruptedException e) { e.printStackTrace(); }

				// Handle non-received messages
				missing = this.getMissingMessages();
				for (LinkedList<Integer> missingMessage : missing) {
					if (Config.OUTPUT_MISSING_MESSAGES) System.out.println("I ("+this.clientID+") did not receive message "+FormatMessagePath(missingMessage));
					this.HandleSubMsg(missingMessage, Config.DEFAULT_VALUE);
				}
				this.nextExpectedRound();
			}
		}
		// Decide output
		if (this.faultLevel == 0) System.out.println("I ("+this.clientID+") have decided on " + this.data.Decide());
		else 											System.out.println("I ("+this.clientID+") am a faulty process so my decision does not matter =)");

		//System.out.println(this.data);

		/*
		if(Config.OUTPUT_DEBUGDATA > 0 && this.clientID == Config.CLIENT_ID[Config.CLIENT_ID.length-1]) {
			System.out.println("Number of messages sent: " + Config.SENT_MESSAGES);
			System.out.println("Number of messages received: " + Config.RECEIVED_MESSAGES);
		}*/
	}



	public synchronized void HandleSubMsg(LinkedList<Integer> path, int value) {
		LinkedList<Integer> newPath;

		// Add information to DecisionTree
		if (path.size() == 1) this.data = new DecisionTree(path.get(0), value);
		else this.data.AddNewNode(path, value);

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
			//Config.RECEIVED_MESSAGES++;
			if (msg.currentRound == this.currentRound) { // Check round = valid
				if (Config.OUTPUT_RECEIVED_MESSAGES) System.out.println("I ("+this.clientID+") have received round "+this.currentRound+" message from "+msg.sender+"; "+msg.toString());

				for (int i=0; i<msg.paths.size(); i++) { // Handle all sub-messages packed in roundMessage object.
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
			} catch (Exception e) { e.printStackTrace(); }
		} else { // Standard configuration in 1 process.
			initializeRMI(Config.REGISTRY_PORT);

			Component comps[] = new Component[Config.CLIENT_ID.length];

			try {
				for (int i = 0; i < Config.CLIENT_ID.length; ++i)
					comps[i] = new Component(Config.CLIENT_ID[i], "localhost", Config.CLIENT_ID, Config.FAULTS, Config.FL[i], 1000);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}