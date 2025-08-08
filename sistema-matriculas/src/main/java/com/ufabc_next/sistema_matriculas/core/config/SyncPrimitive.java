package com.ufabc_next.sistema_matriculas.core.config;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;


public class SyncPrimitive implements Watcher {


    public static ZooKeeper zk = null;
    public static Integer mutex;

    @Value("server.address")
    public static String address;


    protected String root;

    public SyncPrimitive() {
        if(zk == null){
            try {
                System.out.println("Starting ZK:");
                zk = new ZooKeeper(address, 3000, this);
                mutex = Integer.valueOf(-1);
                System.out.println("Finished starting ZK: " + zk);
            } catch (IOException e) {
                System.out.println(e.toString());
                zk = null;
            }
        }
        //else mutex = Integer.valueOf(-1);
    }

    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            //System.out.println("Process: " + event.getType());
            mutex.notify();
        }
    }


}
