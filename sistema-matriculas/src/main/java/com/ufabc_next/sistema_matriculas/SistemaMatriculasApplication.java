package com.ufabc_next.sistema_matriculas;

import com.ufabc_next.sistema_matriculas.domain.common.Barrier;
import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive.zk;
import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.leaderElection;

@SpringBootApplication
public class SistemaMatriculasApplication {

	public static void main(String[] args) throws InterruptedException, KeeperException {

		SpringApplication.run(SistemaMatriculasApplication.class, args);

		Queue queue = new Queue("host.docker.internal", "/communication-queue");
		System.out.println("queue created" + queue);

		zk.getChildren("/communication-queue" , queue);


		Barrier barrier = new Barrier("host.docker.internal:2181", "/barrier");
		barrier.enter();

		System.out.println("Entered barrier");



	}

}
