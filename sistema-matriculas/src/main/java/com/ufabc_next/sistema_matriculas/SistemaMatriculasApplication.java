package com.ufabc_next.sistema_matriculas;

import com.ufabc_next.sistema_matriculas.domain.common.Barrier;
import com.ufabc_next.sistema_matriculas.domain.common.Leader;
import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.KeeperException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive.zk;

@SpringBootApplication
public class SistemaMatriculasApplication {

    public static void main(String[] args) throws InterruptedException, KeeperException {
        SpringApplication.run(SistemaMatriculasApplication.class, args);

        Queue queue = new Queue("host.docker.internal", "/communication-queue");
        System.out.println("queue created" + queue);

        zk.getChildren("/communication-queue", queue);

        System.out.println("Starting barrier ");

        Barrier barrier = new Barrier("host.docker.internal:2181", "/barrier");
        barrier.enter();

        System.out.println("Entered barrier");

        // Para os seguidores, adicione um loop de consumo
        while (!Leader.isLeader()) {
            System.out.println("Sou seguidor, iniciando consumo de mensagens");
            String message = queue.consume();
            System.out.println("Mensagem consumida: " + message);
            // Opcional: adicione um pequeno delay para não sobrecarregar
            Thread.sleep(1000);
        }
    }

}
