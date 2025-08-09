package com.ufabc_next.sistema_matriculas.domain.common;

import com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Barrier extends SyncPrimitive {
    private final int size;
    private String name;

    public Barrier( String root, int size) {
        this.root = root;
        this.size = size;
        if (zk != null) {
            try {
                Stat var4 = zk.exists(root, false);
                if (var4 == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException var6) {
                System.out.println("Keeper exception when instantiating queue: " + var6);
            } catch (InterruptedException var7) {
                System.out.println("Interrupted exception");
            }
        }

        try {
            this.name = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException var5) {
            System.out.println(var5);
        }

    }

    public boolean enter() throws KeeperException, InterruptedException {
        zk.create(this.root + "/" + this.name, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        while (true) {
            synchronized (mutex) {
                List<String> root = zk.getChildren(this.root, true);
                if (root.size() >= this.size) {
                    return true;
                }

                mutex.wait();
            }
        }
    }

    public void leave() throws KeeperException, InterruptedException {
        zk.delete(this.root + "/" + this.name, 0);

        while (true) {
            synchronized (mutex) {
                List<String> root = zk.getChildren(this.root, true);
                if (root.isEmpty()) {
                    return;
                }

                mutex.wait();
            }
        }
    }
}
