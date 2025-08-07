package com.ufabc_next.sistema_matriculas.core.queues;

import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Value;

public class Queues implements Watcher {
    @Value("value.addresses:localhost:2181")
    String address;

    public static void queueTest(String[] args) {
        String address = args[0];
        String queueName = args[1];
        int queueSize = Integer.parseInt(args[2]);
        String queueOperation = args[3];


        Queue q = new Queue(address, queueName);

        System.out.println("Input: " + queueName);
        int i;

        if (queueOperation.equals("p")) {
            System.out.println("Producer");
            for (i = 0; i < queueSize; i++)
                try {
                    q.produce(10 + i);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        } else {
            System.out.println("Consumer");

            for (i = 0; i < queueSize; i++) {
                try {
                    int r = q.consume();
                    System.out.println("Item: " + r);
                } catch (KeeperException e) {
                    i--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
