package com.ufabc_next.sistema_matriculas.core.leaderElection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

import com.ufabc_next.sistema_matriculas.domain.common.Leader;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class LeaderElection implements Watcher {

    public static void leaderElection(String args[]) {
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