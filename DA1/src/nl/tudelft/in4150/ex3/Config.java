/**
 * @author N. de Jong
 * @author T. Zandvliet
 */
package nl.tudelft.in4150.ex3;

/** Configuration */
public class Config {
	/*public static int[] CLIENT_ID = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16}; // Define the client id's
	public static int[] FL = {0,2,0,2,0,0,0,0,0,1,0,0,0,0,0,0,0};
	public static int FAULTS = 3; *//* Do NOT increase to 4; SystemOutOfHeapSpace will be thrown ;)
																	 Think about it: a minimum of 13 clients is needed for 4 faults to be acceptable, so the minimum amount of memory
																	 is at least: 1 * 13 * 12 * 11 * 10 = 17160 ints. * 13 processes = 223080 ints. = ~>8MB IF OPTIMALLY IMPLEMENTED.
																	 For 16 processes it will already be >27 MB (OPTIMAL!), and so on...
																 */
	public static int ROUND_TIME_A[] = {1000/10*8, 1000/10*8, 2200/10*8, 15000/10*8, 30000/10*8};
	public static int ROUND_TIME_B[] = {1000/10*2, 1000/10*2, 2200/10*2, 15000/10*2, 30000/10*2};
	//public static int ROUND_TIME_A[] = {2000/10*8, 2000/10*8, 3200/10*8, 15000/10*8, 30000/10*8};
	//public static int ROUND_TIME_B[] = {2000/10*2, 2000/10*2, 3200/10*2, 15000/10*2, 30000/10*2};
	//Working
	public static int[] CLIENT_ID = {0,1,2,3,4,5,6,7,8,9}; // Define the client id's
	public static int[] FL = {0,2,0,2,0,0,0,0,0,1};
	public static int FAULTS = 3;
	/*//Working
	public static int[] CLIENT_ID = {0,1,2,3,4,5,6}; // Define the client id's
	public static int[] FL = {0,2,0,1,0,0,0};
	public static int FAULTS = 2;*/
	//public static int ROUND_TIME = 500;*/


	public static int INITIAL_VALUE = 1;
	public static int DEFAULT_VALUE = 0;
	public static boolean OUTPUT_RECEIVED_MESSAGES = false;
	public static boolean OUTPUT_MISSING_MESSAGES = false;
	public static boolean OUTPUT_OUTOFDATE_MESSAGES = true;
	public static boolean OUTPUT_MESSAGE_COUNTS_INDIV = false;
	public static boolean OUTPUT_MESSAGE_COUNTS_SUM = false;
	public static int SENT_MESSAGES[] = new int[CLIENT_ID.length];
	public static int RECEIVED_MESSAGES[] = new int[CLIENT_ID.length];

	public static int REGISTRY_PORT = 1099; // Setting: port of RMI registry.

	public static int MIN_DELAY = 100; // Setting: delay interval for messages (transfer times)
	public static int MAX_DELAY = 100; // This is strictly necessary, because it functions as the synchronization-deviation.
}
