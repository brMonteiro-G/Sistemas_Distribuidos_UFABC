package com.ufabc_next.sistema_matriculas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.leaderElection;

@SpringBootApplication
public class SistemaMatriculasApplication {

	public static void main(String[] args) {

		SpringApplication.run(SistemaMatriculasApplication.class, args);

		leaderElection(args);
	}

}
