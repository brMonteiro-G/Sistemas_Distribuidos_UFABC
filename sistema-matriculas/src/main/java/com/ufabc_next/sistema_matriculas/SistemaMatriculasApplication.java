package com.ufabc_next.sistema_matriculas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SistemaMatriculasApplication {

	public static void main(String[] args) throws InterruptedException, KeeperException {

		SpringApplication.run(SistemaMatriculasApplication.class, args);

		Queue queue = new Queue("host.docker.internal", "/communication-queue");
		System.out.println("queue created" + queue);

		zk.getChildren("/communication-queue" , queue);

		leaderElection(args);



	}
    public static void main(String[] args) {
        SpringApplication.run(SistemaMatriculasApplication.class, args);

        try {

            System.out.println("Starting barrier ");

            Barrier barrier = new Barrier("host.docker.internal:2181", "/barrier");
            barrier.enter();
            System.out.println("Entered barrier: /barrier");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
