/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

/** Configuration */
public class Config {
	public static int CLIENT_COUNT = 10; // Setting: amount of clients to be initiated.
	//public static int CLIENT_INIT = 0; // Not a setting; this is a shared memory variable that contains the number of already initiated clients.
	public static int[] CLIENT_ID = {9, 2, 5, 3, 7, 11, 1, 15, 13, 8}; // Define the client id's

	//public static int lastMsgID = 0; // Not a setting; this is a shared memory variable that contains the ID of the last message created.

	public static int REGISTRY_PORT = 1099; // Setting: port of RMI registry.

	public static int MIN_DELAY = 500; // Setting: delay interval for messages (transfer times)
	public static int MAX_DELAY = 500;
}
