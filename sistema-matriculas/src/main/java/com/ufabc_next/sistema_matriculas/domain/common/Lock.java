package com.ufabc_next.sistema_matriculas.domain.common;

import com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lock extends SyncPrimitive {
    private static final Logger log = LoggerFactory.getLogger(Lock.class);
    long wait;
    String pathName;

    public Lock(String address, String name) {
        super(address);
        this.root = name;
        this.wait = 100000000;

        if (zk != null) {
            try {
                var lock = zk.exists(root, false);
                if (lock == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                log.error("Error on god knows what {}", e.toString());
            } catch (InterruptedException e) {
                log.error("Interrupted exception ixi {}", e.toString());
            }
        }
    }

    public boolean lock(String message, int lock_size) throws KeeperException, InterruptedException {
        var children = zk.getChildren(root, false);

        if (children.size() >= lock_size) {
            log.info("Max number of locks reached ({}). Blocking new writes.", lock_size);
            return false;
        }

        pathName = zk.create(root + "/lock-", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Creating lock at " + pathName);
        log.info("Created lock {}", pathName);

        return true;
    }

    boolean     testMin(String message) throws KeeperException, InterruptedException {
        while (true) {
            var pathStats = zk.exists(root+"/"+message, this);
            log.info("Now watching {}", pathStats);
            if (pathStats != null) {
                log.info("Watching for {}", pathStats.toString());
                break;
            }
        }
        log.info("Children is waiting for a ping {}", pathName);
        return false;
    }

    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            var path = event.getPath();
            if (event.getType() == Event.EventType.NodeDeleted) {
                log.info("{} has been deleted", path);
            }
            try {
                System.out.println(event);
               if (testMin(event.getPath())) {
                   this.compute();
               } else {
                   log.info("Not lowest sequence number! Waiting for a new notification.");
               }
            }  catch (Exception e) {
                log.error("Error on process {}", e.toString());
                e.printStackTrace();
            }
        }
    }

    public void compute() {
        log.info("Computing lock {}", pathName);
        try {
            new Thread().sleep(wait);
        } catch (InterruptedException e) {
            log.error("Interrupted exception ixi {}", e.toString());
            e.printStackTrace();
        }
        log.info("released");
        System.exit(0);
    }


}
