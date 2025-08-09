package com.ufabc_next.sistema_matriculas.core.barriers;

import com.ufabc_next.sistema_matriculas.domain.common.Barrier;
import org.apache.zookeeper.KeeperException;

import java.util.Random;

public class BarrierService {

    public static void barrierCreation(String[] args) {

        Barrier barrier = new Barrier( "host.docker.internal","/b1", Integer.parseInt(args[1]));


        try {
            if (!barrier.enter()) {
                System.out.println("Error when entering the barrier");
            }
            System.out.println("Entered barrier: " + args[1]);
        } catch (KeeperException | InterruptedException var9) {
        }

        Random barrier1 = new Random();
        int var3 = barrier1.nextInt(100);

        for (int var4 = 0; var4 < var3; ++var4) {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException var8) {
            }
        }

        try {
            barrier.leave();
        } catch (KeeperException | InterruptedException var6) {
        }

        System.out.println("Left barrier");
    }
}