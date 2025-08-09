package com.ufabc_next.sistema_matriculas.core.queues;

import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Objects;

public class Queues implements Watcher {


      public static void queueTest(String operation) throws InterruptedException, KeeperException {
        Queue q = new Queue("host.docker.internal", "/communication-queue");
        System.out.println("Iniciando execução da fila");



        int i;
        int batchSize = 10;

        if (Objects.equals(operation, "producer")) {
            System.out.println("fila executada por metodo producer");

            for (i = 0; i < batchSize; i++)
                try{
                    q.produce(10 + i);
                    //checkIfIsLeaderAndProduce();
                } catch (KeeperException | InterruptedException e){
                    System.out.println("Deu erro " + e);

                    throw e;
                }

        } else {
            System.out.println("Consumer");

            int batchConsumerSize = 2;


            for (i = 0; i < batchConsumerSize; i++) {
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
