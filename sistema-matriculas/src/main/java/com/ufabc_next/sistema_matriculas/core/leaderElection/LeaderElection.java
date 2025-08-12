package com.ufabc_next.sistema_matriculas.core.leaderElection;

import com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive;
import com.ufabc_next.sistema_matriculas.domain.common.Leader;
import com.ufabc_next.sistema_matriculas.domain.common.Queue;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Random;

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
                Queue queue = new Queue("host.docker.internal", "/communication-queue");

                // Registrar watcher para mudanças no líder
                SyncPrimitive.zk.exists("/leader", event -> {
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

                // Registrar watcher para mensagens na fila
                SyncPrimitive.zk.getChildren("/communication-queue", queue);

                synchronized (mutex) {
                    while (true) {
                        try {
                            System.out.println("Aguardando eventos (nova eleição ou mensagens)...");
                            mutex.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
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