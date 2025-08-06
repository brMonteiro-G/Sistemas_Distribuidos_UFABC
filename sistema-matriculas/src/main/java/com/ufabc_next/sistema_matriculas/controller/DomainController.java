package com.ufabc_next.sistema_matriculas.controller;

import org.springframework.web.bind.annotation.*;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.leaderElection;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.queueTest;


@RestController
@RequestMapping("/leader")
public class DomainController {

        @PostMapping("/elect")
        public void electLeader(@RequestBody String[] args) {
            leaderElection(args);
        }

        @PostMapping("/elect")
        public void QueuesTest(@RequestBody String[] args) {
            queueTest(args)
        }
}
