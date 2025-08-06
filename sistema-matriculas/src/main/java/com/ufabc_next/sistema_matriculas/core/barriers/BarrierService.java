package com.ufabc_next.sistema_matriculas.core.barriers;

import com.ufabc_next.sistema_matriculas.domain.common.Barrier;
import org.apache.zookeeper.KeeperException;

import java.util.Random;

public class BarrierService {

    public static void barrierCreation(String[] var0) {
        Barrier var1 = new Barrier(var0[1], "/b1", Integer.valueOf(var0[2]));

        try {
            boolean var2 = var1.enter();
            System.out.println("Entered barrier: " + var0[2]);
            if (!var2) {
                System.out.println("Error when entering the barrier");
            }
        } catch (KeeperException | InterruptedException var9) {
        }

        Random var11 = new Random();
        int var3 = var11.nextInt(100);

        for (int var4 = 0; var4 < var3; ++var4) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException var8) {
            }
        }

        try {
            var1.leave();
        } catch (KeeperException | InterruptedException var6) {
        }

        System.out.println("Left barrier");
    }
}