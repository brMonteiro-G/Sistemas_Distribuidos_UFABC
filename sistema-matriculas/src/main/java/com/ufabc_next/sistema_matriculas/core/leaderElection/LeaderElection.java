package com.ufabc_next.sistema_matriculas.core.leaderElection;

import com.ufabc_next.sistema_matriculas.domain.common.Leader;
import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Random;

import static com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive.zk;

public class LeaderElection implements Watcher {
    private static final Object mutex = new Object();

    public static void leaderElection(String[] args) {
        Random rand = new Random();
        int r = rand.nextInt(1_000_000);
        Leader leader = new Leader("host.docker.internal", "/election", "/leader", r);

        try {
            boolean success = leader.elect();
            if (success) {
                leader.compute();
            } else {
                // Criar watcher para nova eleição e mensagens
                System.out.println("Não sou líder, aguardando eleição...");
                // Registrar watcher para mudanças no líder
                zk.exists("/leader", event -> {
                    if (event.getType() == Event.EventType.NodeDeleted) {
                        System.out.println("Líder morreu, iniciando nova eleição...");
                        try {
                            if (leader.elect()) {
                                leader.compute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }
}