package com.ufabc_next.sistema_matriculas.domain.common;

import com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;

public class Lock extends SyncPrimitive {
    long wait;
    String pathName;

    /**
     * Constructor of lock
     *
     * @param address
     * @param name    Name of the lock node
     */
    public Lock(String address, String name, long waitTime) {
        super(address);
        this.root = name;
        this.wait = waitTime;
        // Create ZK node name
        if (zk != null) {
            try {
                var s = SyncPrimitive.zk.exists(root, false);
                if (s == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                System.out.println("Keeper exception when instantiating queue: " + e.toString());
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
    }

    public boolean lock() throws KeeperException, InterruptedException {
        //Step 1
        pathName = zk.create(root + "/lock-", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("My path name is: " + pathName);
        //Steps 2 to 5
        return testMin();
    }

    boolean testMin() throws KeeperException, InterruptedException {
        while (true) {
            Integer suffix = Integer.valueOf(pathName.substring(12));
            //Step 2
            var list = zk.getChildren(root, false);
            Integer min = Integer.valueOf(list.get(0).substring(5));
            System.out.println("List: " + list.toString());
            String minString = list.get(0);
            for (String s : list) {
                Integer tempValue = Integer.valueOf(s.substring(5));
                //System.out.println("Temp value: " + tempValue);
                if (tempValue < min) {
                    min = tempValue;
                    minString = s;
                }
            }
            System.out.println("Suffix: " + suffix + ", min: " + min);
            //Step 3
            if (suffix.equals(min)) {
                System.out.println("Lock acquired for " + minString + "!");
                return true;
            }
            //Step 4
            //Wait for the removal of the next lowest sequence number
            Integer max = min;
            String maxString = minString;
            for (String s : list) {
                Integer tempValue = Integer.valueOf(s.substring(5));
                //System.out.println("Temp value: " + tempValue);
                if (tempValue > max && tempValue < suffix) {
                    max = tempValue;
                    maxString = s;
                }
            }
            //Exists with watch
            var s = zk.exists(root + "/" + maxString, this);
            System.out.println("Watching " + root + "/" + maxString);
            //Step 5
            if (s != null) {
                //Wait for notification
                break;
            }
        }
        System.out.println(pathName + " is waiting for a notification!");
        return false;
    }

    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            String path = event.getPath();
            if (event.getType() == Event.EventType.NodeDeleted) {
                System.out.println("Notification from " + path);
                try {
                    if (testMin()) { //Step 5 (cont.) -> go to step 2 to check
                        this.compute();
                    } else {
                        System.out.println("Not lowest sequence number! Waiting for a new notification.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void compute() {
        System.out.println("Lock acquired!");
        try {
            new Thread().sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Exits, which releases the ephemeral node (Unlock operation)
        System.out.println("Lock released!");
        System.exit(0);
    }
}