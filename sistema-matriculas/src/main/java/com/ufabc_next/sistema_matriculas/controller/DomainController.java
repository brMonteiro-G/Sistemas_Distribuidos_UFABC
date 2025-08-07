package com.ufabc_next.sistema_matriculas.controller;

import org.springframework.web.bind.annotation.*;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.leaderElection;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.queueTest;


@RestController
@RequestMapping("/domain")
public class DomainController {

        @PostMapping("/elect")
        public void electLeader(@RequestBody String[] args) {
            leaderElection(args);
        }

        @PostMapping("/queue")
        public void QueuesTest(@RequestBody String[] args) {
            queueTest(args);
        }

        @PostMapping("/lock")
        public void lock(@RequestBody String[] args) {
            Locks.exec(args);
        }
    @PostMapping("/elect")
    public void electLeader(@RequestBody String[] args) {
        leaderElection(args);
    }

    @PostMapping("/barrier")
    public void createBarrier(@RequestBody String[] args) {
        barrierCreation(args);
    }
}
