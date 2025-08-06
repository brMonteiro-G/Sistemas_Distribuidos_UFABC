package com.ufabc_next.sistema_matriculas.core.locks;

import java.util.concurrent.locks.Lock;

public class Locks {

  public static void exec(String args[]) {
    	var lock = new Lock(args[1],"/lock",Long.valueOf(args[2]));
        try{
        	var success = lock.lock();
        	if (success) {
        		lock.compute();
        	} else {
        		while(true) {
        			//Waiting for a notification
        		}
            }
        } catch (KeeperException e){
        	e.printStackTrace();
        } catch (InterruptedException e){
        	e.printStackTrace();
        }
    }
}
