package com.ufabc_next.sistema_matriculas.core.queues;

import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Queues implements Watcher {


    public static String processRequestMessage(String operation, String message) throws InterruptedException, KeeperException {
        Queue q = new Queue("host.docker.internal", "/communication-queue");
        System.out.println("Iniciando execução da fila");


        int i;
        int batchSize = 10;

        if (Objects.equals(operation, "producer")) {
            System.out.println("fila executada por metodo producer");

            for (i = 0; i < batchSize; i++)
                try{
                    return q.produce(message);

                    //checkIfIsLeaderAndProduce();
                } catch (KeeperException | InterruptedException e){
                    System.out.println("Deu erro " + e);

                    throw e;
                }


        } else {
            System.out.println("Consumer");

            int batchConsumerSize = 2;
            List consumedMessages = new ArrayList();

            for (i = 0; i < batchConsumerSize; i++) {
                try{
                    String postedMessage = q.consume();
                    consumedMessages.add(postedMessage);
                    System.out.println("Item: " + postedMessage);
                } catch (KeeperException e){
                    i--;
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return consumedMessages.toString();

        }
        return "operação executada";
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}