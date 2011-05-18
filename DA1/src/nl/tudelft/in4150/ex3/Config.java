/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

/** Configuration */
public class Config {
	public static int[] CLIENT_ID = {0,1,2,3,4,5,6}; // Define the client id's
	public static int[] FL = {2,0,2,0,0,0,0}; // Define the client id's
	public static int FAULTS = 2;
	public static int INITIAL_VALUE = 1;
	public static int DEFAULT_VALUE = 0;

	public static int ROUND_TIME = 1000;
	public static int OUTPUT_DEBUGDATA = 0;

	public static int REGISTRY_PORT = 1099; // Setting: port of RMI registry.

	public static int MIN_DELAY = 75; // Setting: delay interval for messages (transfer times)
	public static int MAX_DELAY = 125;
}
