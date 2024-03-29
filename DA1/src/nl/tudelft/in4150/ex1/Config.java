/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex1;

/** Configuration */
public class Config {

	/** Settings */
	public static int CLIENT_COUNT = 5; // Setting: amount of clients to be initiated.
	public static int[] CLIENT_DELAY = {100, 200, 300, 400, 500}; // Setting: send delay between 2 broadcasts for each client.

	public static int REGISTRY_PORT = 1099; // Setting: port of RMI registry.

	public static int MIN_DELAY = 0; // Setting: delay interval for messages (transfer times)
	public static int MAX_DELAY = 1000;

	public static boolean DEBUG_SEND = false; // Setting: defines what is output to console.
	public static boolean DEBUG_RECV = true;

	public static double P_RND_MSG_SEL = 0.2; // Probability of selecting a message of the delivered list to a message (as 'dependancy' of the message)

	public static boolean BYZANTINE_ENABLED = true; // Are intended malfunctions allowed? (to test malfunction detection)
	public static double P_RND_BYZANTINE_ERROR = 0.02; // Chance of malfunction occurence.

	/** Non-settings / interprocess variables */
	public static int CLIENT_INIT = 0; // Not a setting; this is a shared memory variable that contains the number of already initiated clients.
	public static int lastMsgID = 0; // Not a setting; this is a shared memory variable that contains the ID of the last message created.
}
