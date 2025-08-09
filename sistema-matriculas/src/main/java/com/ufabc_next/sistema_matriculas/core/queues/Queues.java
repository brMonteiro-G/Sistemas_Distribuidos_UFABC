package com.ufabc_next.sistema_matriculas.core.queues;

import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class Queues implements Watcher {
      public static void queueTest(String args[]) {
        Queue q = new Queue("host.docker.internal", "/app3");


        System.out.println("Input: " + args[1]);
        int i;
        Integer max = Integer.valueOf(args[2]);

        if (args[3].equals("p")) {
            System.out.println("Producer");
            for (i = 0; i < max; i++)
                try{
                    q.produce(10 + i);
                } catch (KeeperException e){
                    e.printStackTrace();
                } catch (InterruptedException e){
			    e.printStackTrace();
                }
        } else {
            System.out.println("Consumer");

            for (i = 0; i < max; i++) {
                try{
                    int r = q.consume();
                    System.out.println("Item: " + r);
                } catch (KeeperException e){
                    i--;
                } catch (InterruptedException e){
			    e.printStackTrace();
                }
            }
        }
    }

        @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
