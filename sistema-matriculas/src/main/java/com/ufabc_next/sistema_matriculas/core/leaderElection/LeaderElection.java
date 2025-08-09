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
        int randomNumber = rand.nextInt(1000000);
        Leader leader = new Leader("host.docker.internal","/election","/leader",randomNumber);

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

    public static void produce(String args[]) {
        // Generate random integer
        Random rand = new Random();
        int randomNumber = rand.nextInt(1000000);
        Leader leader = new Leader("host.docker.internal","/election","/leader",randomNumber);
        try{
            leader.checkIfIsLeaderAndProduce();
        } catch (KeeperException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    public static void consume(String args[]) {
        // Generate random integer
        Random rand = new Random();
        int randomNumber = rand.nextInt(1000000);
        Leader leader = new Leader("host.docker.internal","/election","/leader",randomNumber);
        try{
            leader.checkIfIsConsumer();

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