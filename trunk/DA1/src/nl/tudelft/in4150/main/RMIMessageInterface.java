/**
 * RMIMESSAGEINTERFACE CLASS
 * in4150, exercise 1
 * 
 * @version 2011/02/03
 * @author Thijs Zandvliet
 * 
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */

package nl.tudelft.in4150.main;

import java.rmi.*;

public interface RMIMessageInterface extends Remote
{
	
	 public String receive() throws RemoteException;
	 
	 public void broadcast(String msg, int from) throws RemoteException;

}
