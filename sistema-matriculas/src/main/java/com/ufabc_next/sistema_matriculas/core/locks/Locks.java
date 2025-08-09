package com.ufabc_next.sistema_matriculas.core.locks;

import com.ufabc_next.sistema_matriculas.domain.common.Lock;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class Locks implements Watcher {
    public static void exec(String args[]) {
        var lock = new Lock("host.docker.internal", "/lock", Long.valueOf(args[2]));

        try {
            var success = lock.lock();
            if (success) {
                lock.compute();
            } else {
                while (true) {
                    //Waiting for a notification
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
