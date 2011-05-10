/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

import nl.tudelft.in4150.ex1.*;
import nl.tudelft.in4150.ex1.Config;

import java.util.Arrays;
import java.util.ArrayList;

public class Component extends RMIClient implements Runnable {
	private int order;
	private int[] values;
	private int[] values_accent;


	/** Create a new client */
	public Component(int clientID, String registryServer, int[] clients) throws java.rmi.RemoteException {
		super(clientID, registryServer, clients);

		this.values = new int[clients.length];
		this.values_accent = new int[clients.length];
		//Arrays.fill(this.values, 0); // fill the array with default values

		//System.out.println(this.clientID + Arrays.toString(this.defineIDs()));

		new Thread(this).start();
	}

	public void run() {
		// Synchronized looping
		// In each round M
			// For each process do
				// SendMessages code
			//<
		//<
		// For each process do
			// If I am not IsFaulty
				// Decide
			// Else
				// Output I am faulty
			//<
		//<

	}

	public void startSynchronizedLoop()

	/** Broadcast message to lieutenants */
	public void broadcast(int faults, int value, ArrayList<Integer> lieutenants) {
		for (int i=0; i<this.clients.length; i++)
			//if (i!=this.clientID) // Should we also broadcast to ourselves?
				//if (i>0) // Should we also involve original commander?
					this.sendMessage(new Message(faults, value, this.clientID, lieutenants), this.clients[i]);
	}

	/** Code executed by the lieutenant */
	public synchronized void onMessageReceived(Message message) {
		if (message.faults == 0) {
			//
		} else {
		this.values[message.commander] = message.value;
			//
			this.broadcast(message.id+1, message.faults-1, message.initialValue); // TODO: check the message round
			this.majority();
		}
		//////
		// mNodes[ path ] = node
	}

	/** Define the value with the most occurrences in the array */
	public void majority() {
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
		//////// input: path
		// n = mChildren[ path ].size
		//
	}

	/** Create multiple clients, based on configuration */
	public static void main(String[] args) {
		if (args.length > 0) { // Multi process version. 1st param: my ID. 2nd param: registry server. 3rd param: client count. 4rd param: sleep interval at start. 5th param: am I registry?
			try {
				if (args[4].equals("true")) initializeRMI(Config.REGISTRY_PORT);

				int[] clients = new int[args[2]]; for (int i=0; i<Integer.parseInt(args[2]); i++) clients[i] = i;
				Component me = new Component(Integer.parseInt(args[0]), args[1], clients);

				try { Thread.sleep(Integer.parseInt(args[3])); } // Wait for everyones initialization.
				catch (InterruptedException e) { e.printStackTrace(); }

				ArrayList<Integer> initList = new ArrayList<Integer>();
				initList.add(me.clientID);
				if (args[3].equals("true")) me.broadcast(Config.FAULTS, Config.INITIAL_VALUE, initList);
			} catch (Exception e) { e.printStackTrace(); }
			
		} else { // Standard configuration in 1 process.
			initializeRMI(Config.REGISTRY_PORT);

			Component comps[] = new Component[Config.CLIENT_ID.length];

			try {
				for (int i = 0; i < Config.CLIENT_ID.length; ++i)
					comps[i] = new Component(Config.CLIENT_ID[i], "localhost", Config.CLIENT_ID);
			} catch (Exception e) { e.printStackTrace(); }

			try { Thread.sleep(1000); } // Wait for everyones initialization.
			catch (InterruptedException e) { e.printStackTrace(); }

			ArrayList<Integer> initList = new ArrayList<Integer>();
			initList.add(0);
			comps[0].broadcast(Config.FAULTS, Config.INITIAL_VALUE, initList);	// the commander sends a broadcast to the lieutenants
		}
	}
}
