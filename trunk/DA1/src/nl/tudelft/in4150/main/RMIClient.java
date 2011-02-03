/**
 * RMICLIENT CLASS
 * in4150, exercise 1
 * 
 * @version 2011/02/03
 * @author Thijs Zandvliet
 * 
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */

package nl.tudelft.in4150.main;

import java.rmi.*;
import java.rmi.registry.*;


public class RMIClient
{

	/**
	 * Set server settings and send a message
	 * 
	 * @param args
	 * @throws RemoteException
	 */
	public static void main(String args[]) throws RemoteException
	{
		RMIMessageInterface server;
		Registry registry;
		String serverAddress = "localhost";
		int serverPort = 1099;
		String text = "Test";
		
		System.out.println("Sending " + text + " to " + serverAddress + ":" + serverPort);
		try
		{
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(serverPort)).intValue());
			server = (RMIMessageInterface)registry.lookup("RMIServer");
			server.broadcast(text, 1);
			server.receive();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
