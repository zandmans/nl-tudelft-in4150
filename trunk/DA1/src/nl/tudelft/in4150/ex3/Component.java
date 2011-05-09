/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

import java.util.Arrays;

public class Component extends RMIClient {
	private int id; // own id
	private int order;
	private int value;
	private int[] values;
	private int expectedMessage;

	/** Create a new client */
	public Component(int clientID, String registryServer, int[] clients) throws java.rmi.RemoteException {
		super(clientID, registryServer, clients);

		this.id = clientID; // retrieve own id

		this.values = new int[Config.CLIENT_COUNT];
		Arrays.fill(this.values, 0); // fill the array with default values

		this.expectedMessage = 1;
	}

	/** Broadcast message to lieutenants */
	public synchronized void broadcast(int msgID, int faults, int value) {
		int[] lieutenants = this.defineIDs();
		for (int i = 1; i < lieutenants.length; ++i)
			this.sendMessage(new Message(msgID, faults, value, this.id, lieutenants), lieutenants[i]);
	}

	/** Code executed by the lieutenant */
	public synchronized void onMessageReceived(Message message) {
		this.values[message.commander] = message.initialValue;
		if(message.faults == 0) this.majority();
		else this.broadcast(message.id+1, message.faults-1, message.initialValue); // TODO: check the message round
	}

	/** The commander changes during the algorithm, so the lieutenants have to be defined */
	public int[] defineIDs()
	{
		int[] ids = new int[Config.CLIENT_COUNT-1];
		int j = 0;
		for(int i = 1; i < Config.CLIENT_COUNT-1; i++) {
			if(Config.CLIENT_ID[i] != this.id) {
				ids[j] = Config.CLIENT_ID[i];
				j++;
			}
		}
		return ids;
	}

	/** Define the value with the most occurrences in the array */
	public void majority()
	{
		int major = this.values[0];
		int count = 1;
		for(int i = 0; i < this.values.length; i++) {
			if(major == this.values[i] && this.values[i] > 0) count++;
			else if(count == 0) {
				major = this.values[i];
				count = 1;
			}
			else count--;
		}

		this.order = major;
		System.out.println("" + major);
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		if (args.length > 0) { // Multi process version. 1st param: my ID. 2nd param: registry server. 3rd param: sleep interval at start. 4th param: do I broadcast? 5th param: am I registry?
			try {
				if (args[4].equals("true")) initializeRMI(Config.REGISTRY_PORT);

				Component me = new Component(Integer.parseInt(args[0]), args[1], null);

				try { Thread.sleep(Integer.parseInt(args[2])); } // Wait for everyones initialization.
				catch (InterruptedException e) { e.printStackTrace(); }

				if (args[3].equals("true")) me.broadcast(1, Config.FAULTS, Config.INITIAL_VALUE);
			} catch (Exception e) { e.printStackTrace(); }
			
		} else { // Standard configuration in 1 process.
			initializeRMI(Config.REGISTRY_PORT);

			Component comps[] = new Component[Config.CLIENT_COUNT];

			try {
				for (int i = 0; i < Config.CLIENT_COUNT; ++i)
					comps[i] = new Component(Config.CLIENT_ID[i], "localhost", Config.CLIENT_ID);

				comps[0].broadcast(1, Config.FAULTS, Config.INITIAL_VALUE);	// the commander sends a broadcast to the lieutenants
			} catch (Exception e) { e.printStackTrace(); }

			try { Thread.sleep(1000); } // Wait for everyones initialization.
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
}
