package com.ufabc_next.sistema_matriculas.controller;

import com.ufabc_next.sistema_matriculas.core.locks.Locks;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.*;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.queueTest;


@RestController
@RequestMapping("/domain")
public class DomainController {

    @PostMapping("/elect")
    public void electLeader(@RequestBody String[] args) {
        leaderElection(args);
    }


    @PostMapping("/leader/produce")
    public void produceMessagess(@RequestBody String[] args) {
        produce(args);
    }

    @PostMapping("/follower/consume")
    public void consumeMessage(@RequestBody String[] args) {
        consume(args);
    }


    @PostMapping("/queue")
    public void QueuesTest(@RequestBody String[] args) {
        queueTest(args);
    }

    @PostMapping("/lock")
    public void lock(@RequestBody String[] args) {
        Locks.exec(args);
    }
}
