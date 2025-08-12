package com.ufabc_next.sistema_matriculas.domain.common;

import com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Producer-Consumer queue
 */
public class Queue extends SyncPrimitive {


    /**
     * Constructor of producer-consumer queue
     *
     * @param address
     * @param name
     */
    public Queue(String address, String name) {
        super(address);
        this.root = name;
        // Create ZK node name
        if (zk != null) {
            try {
                Stat s = zk.exists(root, false);
                if (s == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                System.out.println("Keeper exception when instantiating queue: " + e);
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
    }

    /**
     * Add element to the queue.
     *
     * @param message
     * @return
     */

    public String produce(String message) throws KeeperException, InterruptedException {

        // metodo
        String leaderidentification = "/leader";

        Stat leaderStat = zk.exists(leaderidentification, false);

        // Já existe líder → pega o ID
        byte[] leaderData = zk.getData(leaderidentification, false, leaderStat);
        String leaderId = new String(leaderData, StandardCharsets.UTF_8);

        // metodo

        System.out.println("id do current leader " + Leader.id);

        if (leaderId.equals(Leader.id)) {
            // Eu sou o líder

            System.out.println("Leader producing message: " + message);
            zk.create(root + "/element", message.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            return "mensagem postada " + message;
        } else {

            System.out.println("não sou lider entao não posso produzir");

            return "não sou lider entao não posso produzir";

        }

    }


    /**
     * Remove first element from the queue.
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String consume() throws KeeperException, InterruptedException {
        int retvalue = -1;
        Stat stat = null;

        System.out.println("Starting consume method");
        // Get the first element available
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren("/communication-queue", this);
                System.out.println("list " + list);
                if (list.isEmpty()) {
                    System.out.println("Going to wait");
                    mutex.wait();
                } else {
                    System.out.println("List is not empty, processing...");
                    int min = Integer.parseInt(list.get(0).substring(7));
                    System.out.println("List: " + list);
                    String minString = list.get(0);
                    for (String s : list) {
                        int tempValue = Integer.parseInt(s.substring(7));
                        System.out.println("Temp value: " + tempValue);
                        if (tempValue < min) {
                            min = tempValue;
                            minString = s;
                        }
                    }
                    System.out.println("Temporary value: " + root + "/" + minString);
                    byte[] b = zk.getData(root + "/" + minString, false, stat);
                    System.out.println("b: " + Arrays.toString(b));
//                    zk.delete(root + "/" + minString, 0);
                    ByteBuffer buffer = ByteBuffer.wrap(b);
                    retvalue = buffer.getChar();
                    return String.valueOf(retvalue);

                }
            }
        }
    }


    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            if (event.getType() == Event.EventType.NodeChildrenChanged && event.getPath().equals("/communication-queue")) {
                System.out.println("Event received: " + event.getType() + " on path: " + event.getPath());
                try {
                    List<String> children = zk.getChildren("/communication-queue", this);
                    for (String child : children) {
                        byte[] data = zk.getData("/communication-queue/" + child, false, null);
                        String message = new String(data, StandardCharsets.UTF_8);
                        System.out.println("Mensagem recebida: " + message);
                    }
                    // colocando o watcher para esperar por novos eventos
                    zk.getChildren("/communication-queue", this);
                } catch (KeeperException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Notifying all waiting threads");
                mutex.notifyAll();
            }
        }
    }
}