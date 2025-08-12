package com.ufabc_next.sistema_matriculas.controller;

import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.*;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.leaderElection;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.processRequestMessage;


@RestController
@RequestMapping("/domain")
public class DomainController {

    @PostMapping("/elect")
    public void electLeader(@RequestBody String[] args) {
        leaderElection(args);
    }

    @PostMapping("/queue/{operation}")
    public String produceMessagess(@PathVariable("operation") String operation, @RequestBody String message) throws InterruptedException, KeeperException {
        return processRequestMessage(operation, message);
    }
}