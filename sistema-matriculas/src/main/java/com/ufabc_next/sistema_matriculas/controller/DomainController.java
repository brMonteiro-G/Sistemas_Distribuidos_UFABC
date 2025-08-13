package com.ufabc_next.sistema_matriculas.controller;

import com.ufabc_next.sistema_matriculas.domain.common.Lock;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.*;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.*;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.processRequestMessage;


@RestController
@RequestMapping("/domain")
public class DomainController {

    @PostMapping("/elect")
    public void electLeader(@RequestBody String[] args) {
        leaderElection(args);
    }

    @PostMapping("/queue/{operation}")
    public String produceMessagess(@PathVariable("operation") String operation, @RequestBody String message ) throws InterruptedException, KeeperException {
        // produce(operation);
        return processRequestMessage(operation, message);
    }

    @PostMapping("/lock")
    public boolean lockStuff(@RequestBody String message) throws InterruptedException, KeeperException {
        Lock lock = new Lock("host.docker.internal", "/communication-queue");
        var result =  lock.lock(message, 10);

        System.out.println("result" + result);

        return result;
    }

    @GetMapping("/health")
    public String health() throws InterruptedException, KeeperException {

return "alive";
    }

//    @PostMapping("/lock")
//    public void lock(@RequestBody String[] args) {
//        Locks.exec(args);
//    }
}