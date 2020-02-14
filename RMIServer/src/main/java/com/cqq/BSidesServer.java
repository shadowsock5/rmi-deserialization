package com.cqq;

//import java.rmi.Naming;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BSidesServer {
	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		try {
			// Create new RMI registry to which we can register
			Registry registry = LocateRegistry.createRegistry(1099);

			// Make our BSides Server object 
			// available under the name "bsides"

			//Naming.rebind("bsides", new BSidesServiceServerImpl()); 
			registry.bind("bsides", new BSidesServiceServerImpl());
			System.out.println("BSides RMI server is ready");
			System.out.println("Java version: " + System.getProperty("java.version"));
		
		} catch (Exception e) {
			// In case of an error, print the stacktrace 
			// and bail out
			e.printStackTrace();
		} 
	}
}

