package com.ufabc_next.sistema_matriculas;

import com.ufabc_next.sistema_matriculas.domain.common.Barrier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SistemaMatriculasApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaMatriculasApplication.class, args);

        try {
            Barrier barrier = new Barrier("host.docker.internal:2181", // endereço do ZooKeeper
                    "/barrier"                 // znode da barreira
            );
            barrier.enter();
            System.out.println("Entered barrier: /barrier");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
