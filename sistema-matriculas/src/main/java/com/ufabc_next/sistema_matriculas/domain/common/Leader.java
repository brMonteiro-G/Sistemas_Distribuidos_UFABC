package com.ufabc_next.sistema_matriculas.domain.common;

import com.ufabc_next.sistema_matriculas.core.config.SyncPrimitive;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.processRequestMessage;




public class Leader extends SyncPrimitive {
    public  static String leader;
    public static  String id; //Id of the leader
    public  String pathName;
    //public  Queue queue = new Queue("host.docker.internal","/teste");


    public Leader(String address, String name, String leader, int id) {
        super(address);
        this.root = name;
        Leader.leader = leader;
        Leader.id = Integer.valueOf(id).toString();
        // Create ZK node name
        if (zk != null) {
            try {
                //Create election znode
                Stat s1 = zk.exists(root, false);
                if (s1 == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                //Checking for a leader
                Stat s2 = zk.exists(leader, false);
                if (s2 != null) {
                    byte[] idLeader = zk.getData(leader, false, s2);
                    System.out.println("Current leader with id: "+new String(idLeader));
                }

            } catch (KeeperException e) {
                System.out.println("Keeper exception when instantiating queue: " + e.toString());
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
    }




    public boolean elect() throws KeeperException, InterruptedException{
        this.pathName = zk.create(root + "/n-", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("My path name is: "+pathName+" and my id is: "+id+"!");
        return check();
    }


    boolean check() throws KeeperException, InterruptedException{
        Integer suffix = Integer.valueOf(pathName.substring(12));
        while (true) {
            List<String> list = zk.getChildren(root, false);
            Integer min = Integer.valueOf(list.get(0).substring(5));
            System.out.println("List: "+list.toString());
            String minString = list.get(0);
            for(String s : list){
                Integer tempValue = Integer.valueOf(s.substring(5));
                //System.out.println("Temp value: " + tempValue);
                if(tempValue < min)  {
                    min = tempValue;
                    minString = s;
                }
            }
            System.out.println("Suffix: "+suffix+", min: "+min);
            if (suffix.equals(min)) {
                this.leader();
                return true;
            }
            Integer max = min;
            String maxString = minString;
            for(String s : list){
                Integer tempValue = Integer.valueOf(s.substring(5));
                //System.out.println("Temp value: " + tempValue);
                if(tempValue > max && tempValue < suffix)  {
                    max = tempValue;
                    maxString = s;
                }
            }
            //Exists with watch
            Stat s = zk.exists(root+"/"+maxString, this);
            System.out.println("Watching "+root+"/"+maxString);
            //Step 5
            if (s != null) {
                //Wait for notification
                break;
            }
        }
        System.out.println(pathName+" is waiting for a notification!");
        return false;

    }

    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                try {
                    boolean success = check();
                    if (success) {
                        compute();
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    void leader() throws KeeperException, InterruptedException {
        System.out.println("Become a leader: "+id+"!");
        //Create leader znode
        Stat s2 = zk.exists(leader, false);
        if (s2 == null) {
            zk.create(leader, id.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } else {
            zk.setData(leader, id.getBytes(), 0);
        }
    }

    public void compute() {
        System.out.println("I will die after 10 seconds!");
        try {
            processRequestMessage("producer", "Hello world I became the leader");

            new Thread().sleep(1000000);
            System.out.println("Process "+id+" died!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

//TODO: nao excluir -- codigo para estudar locks entre nós

//    public void checkIfIsLeaderAndProduce() throws InterruptedException, KeeperException {
//        Stat leaderStat = zk.exists(leader, false);
//
//        // Já existe líder → pega o ID
//        byte[] leaderData = zk.getData(leader, false, leaderStat);
//        String leaderId = new String(leaderData, StandardCharsets.UTF_8);
//
//        if (leaderId.equals(id)) {
//            // Eu sou o líder
//            queue.produce(10);
//
//           // produceAsLeaderAtomic();
//        } else {
//            System.out.println("Sou seguidor, não produzo. Líder atual: " + leaderId);
//        }
//    }
//
//    public void checkIfIsConsumer() throws InterruptedException, KeeperException {
//        Stat leaderStat = zk.exists(leader, false);
//
//        if (leaderStat == null) {
//            // Não existe líder → cria e vira líder
//            zk.create(
//                    leader,
//                    id.getBytes(),
//                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
//                    CreateMode.EPHEMERAL
//            );
//            System.out.println("Eu virei o líder: " + id);
//            produceAsLeaderAtomic();
//            return;
//        }
//
//        // Já existe líder → pega o ID
//        byte[] leaderData = zk.getData(leader, false, leaderStat);
//        String leaderId = new String(leaderData, StandardCharsets.UTF_8);
//
//        if (leaderId.equals(id)) {
//            // Eu sou o líder
//            produceAsLeaderAtomic();
//        } else {
//            System.out.println("Sou seguidor, não produzo. Líder atual: " + leaderId);
//            System.out.println("Sou seguidor, apenas consumo: " + leaderId);
//
//            queue.consume();
//
//        }
//    }
//
//    private void produceAsLeaderAtomic() throws KeeperException, InterruptedException {
//        String message = id + ":42:helloWorld"; // ID do líder + valor qualquer
//        byte[] value = message.getBytes(StandardCharsets.UTF_8);
//
//        // Obter versão atual do znode /leader
//        Stat leaderStat = zk.exists(leader, false);
//        int currentVersion = leaderStat.getVersion();
//
//        System.out.println("vou produzir");
//
////        // Criar lista de operações atômicas
////        List<Op> ops = new ArrayList<>();
////        // 1. Verifica se /leader ainda tem a mesma versão
////        ops.add(Op.check(leader, currentVersion));
////        // 2. Cria o elemento na fila
////        ops.add(Op.create(root + "/n-", value, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL));
////
////        // Executa de forma atômica
////        zk.multi(ops);
////
//        // zk.create(root + "/n-", value, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
//        zk.create(root + "/n-", value, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
//
//
//        System.out.println("Líder " + id + " produziu a mensagem de forma atômica.");
//    }



}
