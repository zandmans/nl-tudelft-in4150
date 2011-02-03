/**
 * RMISERVER CLASS
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
import java.rmi.server.*;
import java.net.*;

public class RMIServer extends UnicastRemoteObject implements RMIMessageInterface
{
	
	private static final long serialVersionUID = 7209980639967338990L;
	public int port;
	public String address;
	public Registry registry;
	public String message;
	
	
	/**
	 * Initialize a new server object
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		try
		{
			RMIServer server = new RMIServer();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	/**
	 * Set the local port and address and start the registry
	 * 
	 * @throws RemoteException
	 */
	public RMIServer() throws RemoteException
	{
		try
		{
			this.address = (InetAddress.getLocalHost()).toString();
		}
		catch(Exception e)
		{
			throw new RemoteException("Can't get inet address !");
		}
		
		this.port = 1099;
		System.out.println("Address=" + this.address + ", port=" + this.port);
		
		try
		{
			registry = LocateRegistry.createRegistry(this.port);
			registry.rebind("RMIServer", this);
		}
		catch(RemoteException e)
		{
			throw e;
		}
	}
	
	
	public void broadcast(String msg, int from) throws RemoteException
	{
		this.message = from + " : " + msg;
	}
	
	
	public String receive() throws RemoteException
	{
		return this.message;
	}

}
