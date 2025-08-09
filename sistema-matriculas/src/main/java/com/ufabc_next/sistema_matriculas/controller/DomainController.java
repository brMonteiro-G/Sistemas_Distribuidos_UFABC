package com.ufabc_next.sistema_matriculas.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ufabc_next.sistema_matriculas.core.barriers.BarrierService.barrierCreation;
import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.leaderElection;
import static com.ufabc_next.sistema_matriculas.core.locks.Locks.exec;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.queueTest;


@RestController
@RequestMapping("/domain")
public class DomainController {


        @PostMapping("/elect")
        public void electLeader(@RequestBody String[] args) {
            leaderElection();
        }


    @PostMapping("/queue")
    public void queuesTest(@RequestBody String[] args) {
        queueTest(args);
    }

    @PostMapping("/lock")
    public void lock(@RequestBody String[] args) {
        exec(args);
    }

    @PostMapping("/barrier")
    public void createBarrier(@RequestBody String[] args) {
        barrierCreation(args);
    }
}