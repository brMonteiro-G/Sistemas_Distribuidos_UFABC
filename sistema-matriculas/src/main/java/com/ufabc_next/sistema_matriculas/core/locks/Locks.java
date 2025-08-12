package com.ufabc_next.sistema_matriculas.core.locks;

import com.ufabc_next.sistema_matriculas.domain.common.Lock;
import org.apache.zookeeper.KeeperException;

public class Locks {

    public static String lockTest(String message) {
        var lock = new Lock(message, "/lock");
        try {
            boolean success = lock.lock(message, 10);
            if (success) {
                lock.compute();
            } else {
                while(true) {
                    //Waiting for a notification
                }
            }
        } catch (KeeperException | InterruptedException e) {
           // log.error("Error on lock {}", e.toString());
        }
        return message;
    }


}
