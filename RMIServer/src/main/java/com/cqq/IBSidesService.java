package com.cqq;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBSidesService extends Remote {

   boolean register(String ticketID) throws RemoteException;
   void visitTalk(String talkname) throws RemoteException;
   void poke(Object attende) throws RemoteException;
}
