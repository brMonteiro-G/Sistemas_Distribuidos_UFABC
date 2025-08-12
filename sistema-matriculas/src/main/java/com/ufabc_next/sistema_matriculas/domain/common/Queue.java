package com.ufabc_next.sistema_matriculas.domain.common;

import com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
                System.out.println("Keeper exception when instantiating queue: " + e.toString());
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
    }

    /**
     * Add element to the queue.
     *
     * @param i
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

            System.out.println("sou lider entao posso produzir");
            zk.create(root + "/element", message.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

            return "mensagem postada " + message;
        }else {

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


        // Get the first element available
        while (true) {
            synchronized (mutex) {
                System.out.println("teste");

                List<String> list = zk.getChildren(this.root, this);
                System.out.println("list " + list);
                if (list.size() == 0) {
                    System.out.println("Going to wait");
                    mutex.wait();
                } else {
                    System.out.println("teste-2");
                    Integer min = Integer.valueOf(list.get(0).substring(7));
                    System.out.println("List: " + list.toString());
                    String minString = list.get(0);
                    for (String s : list) {
                        Integer tempValue = Integer.valueOf(s.substring(7));
                        //System.out.println("Temp value: " + tempValue);
                        if (tempValue < min) {
                            min = tempValue;
                            minString = s;
                        }
                    }
                    System.out.println("Temporary value: " + root + "/" + minString);
                    byte[] b = zk.getData(root + "/" + minString, false, stat);
                    //System.out.println("b: " + Arrays.toString(b));
                    zk.delete(root + "/" + minString, 0);
                    ByteBuffer buffer = ByteBuffer.wrap(b);
                    retvalue = buffer.getChar();
                    return String.valueOf(retvalue);

                }
            }
        }
    }


    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {

            System.out.println("event type for queue watcher " + event.getType());
            System.out.println("event path for queue watcher " + event.getPath());

            if (event.getType() == Event.EventType.NodeChildrenChanged) {

                System.out.println("data changed on path for queue ");
                try {
                    zk.getChildren("/communication-queue", this);
                } catch (KeeperException | InterruptedException e) {
                    throw new RuntimeException(e);
                }


                try {

                    System.out.println("success on queue message");

                } catch (Exception e) {e.printStackTrace();}
                mutex.notify();

            }
        }
    }


}