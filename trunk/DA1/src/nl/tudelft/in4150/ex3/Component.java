/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;
import java.util.LinkedList;

public class Component extends RMIClient implements Runnable {
	private DecisionTree data;
	private int maxFaults;

	private int faultLevel;

	// Vary per round
	private Message roundMessage;
	private int currentRound;
	private boolean receivedFrom[];

	/** Create a new client */
	public Component(int clientID, String registryServer, int[] clients, int maxFaults, int faultLevel) throws java.rmi.RemoteException {
		super(clientID, registryServer, clients);

		this.data = null;
		this.faultLevel = faultLevel;
		this.currentRound = 0;
		this.maxFaults = maxFaults;

		this.receivedFrom = new boolean[clients.length];

		new Thread(this).start();
	}

	public void run() {
		if (this.clientID == 0) {
			LinkedList<Integer> path = CreateNewRootPath();
			int value = 1; Message msg;
			for (int i=1; i<this.clients.length; i++) {
				if (this.faultLevel > 1) value = (Math.random() > 0.5 ? 1 : 0);
				msg = new Message(0, 0); msg.AddSubMsg(path, value);
				if (this.faultLevel < 2) this.sendMessage(msg, i);
			}
			data = new DecisionTree(this.clientID, value);
		} else {
			for (int round = 0; round<=this.maxFaults; round++) {
				// Initialize new round
				this.currentRound = round;
				for (int i=1; i<this.clients.length; i++) receivedFrom[i] = false;
				roundMessage = new Message(round, this.clientID);

				// Send messages
				if (this.faultLevel < 2) {
					for (int i=1; i<this.clients.length; i++) {
						if (this.faultLevel > 1)
							for (int j=0; j<this.roundMessage.values.size(); j++)
								this.roundMessage.values.set(j, (Math.random() > 0.5 ? 1 : 0));
						this.sendMessage(roundMessage, i);
					}
				}

				// Sleep for round time
				try { Thread.sleep(Config.ROUND_TIME); }
				catch (InterruptedException e) { e.printStackTrace(); }

				// Handle non-received messages
				//
			}
			// Decide output
			if (this.faultLevel == 0) System.out.println("I ("+this.clientID+") have decided on " + this.data.Decide());
			else 											System.out.println("I am a faulty process so my decision does not matter =)");
		}
	}

	public LinkedList<Integer> CreateNewRootPath() {
		LinkedList<Integer> path = new LinkedList<Integer>();
		path.add(0);
		return(path);
	}

	public synchronized void HandleSubMsg(LinkedList<Integer> path, int value) {
		this.data.AddNewNode(path, value);
		if (this.currentRound < this.maxFaults && !path.contains(this.clientID)) {
			LinkedList<Integer> newPath = new LinkedList<Integer>(path);
			newPath.add(this.clientID);
			roundMessage.AddSubMsg(newPath, value);
		}
	}

	/** Code executed by the lieutenant */
	public synchronized void onMessageReceived(Message msg) {
		if (this.maxFaults - msg.currentRound == this.currentRound) { // Check round = valid
			System.out.println("I ("+this.clientID+") have received round "+this.currentRound+" message from "+msg.sender);
			this.receivedFrom[msg.sender] = true;

			for (int i=0; i<msg.paths.size(); i++)
				HandleSubMsg(msg.paths.get(i), msg.values.get(i));

		} else System.out.println("I ("+this.clientID+") received OUT-OF-DATE message from "+msg.sender+"; ignoring.");
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		if (args.length > 0) { // Multi process version. 1st param: my ID. 2nd param: registry server. 3rd param: client count. 4th param: max_faults. 5th param: fault_level. 6th param: sleep interval at start. 7th param: am I registry?
			try {
				if (args[7].equals("true")) initializeRMI(Config.REGISTRY_PORT);

				int[] clients = new int[Integer.parseInt(args[2])]; for (int i=0; i<Integer.parseInt(args[2]); i++) clients[i] = i;
				Component me = new Component(Integer.parseInt(args[0]), args[1], clients, Integer.parseInt(args[3]), Integer.parseInt(args[4]));

				try { Thread.sleep(Integer.parseInt(args[5])); } // Wait for everyones initialization.
				catch (InterruptedException e) { e.printStackTrace(); }
			} catch (Exception e) { e.printStackTrace(); }
			
		} else { // Standard configuration in 1 process.
			initializeRMI(Config.REGISTRY_PORT);

			Component comps[] = new Component[Config.CLIENT_ID.length];

			try {
				for (int i = 0; i < Config.CLIENT_ID.length; ++i)
					comps[i] = new Component(Config.CLIENT_ID[i], "localhost", Config.CLIENT_ID, Config.FAULTS, Config.FL[i]);
			} catch (Exception e) { e.printStackTrace(); }

			try { Thread.sleep(1000); } // Wait for everyones initialization.
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
}
