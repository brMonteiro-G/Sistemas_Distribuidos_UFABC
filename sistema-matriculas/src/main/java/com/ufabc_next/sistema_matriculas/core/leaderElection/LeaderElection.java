package com.ufabc_next.sistema_matriculas.core.leaderElection;


import java.util.Random;

import com.ufabc_next.sistema_matriculas.domain.common.Leader;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;


public class LeaderElection implements Watcher {

    public static void leaderElection() {
        // Generate random integer
        Random rand = new Random();
        int r = rand.nextInt(1000000);
        Leader leader = new Leader("host.docker.internal","/election","/leader",r);

        try{
            boolean success = leader.elect();
            if (success) {
                leader.compute();
            } else {
                while(true) {
                    //Waiting for a notification
                }
            }
        } catch (KeeperException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}