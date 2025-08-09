package com.ufabc_next.sistema_matriculas.controller;

import com.ufabc_next.sistema_matriculas.core.locks.Locks;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.*;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.*;
import static com.ufabc_next.sistema_matriculas.core.queues.Queues.queueTest;


@RestController
@RequestMapping("/domain")
public class DomainController {

        @PostMapping("/elect")
        public void electLeader(@RequestBody String[] args) {
            leaderElection(args);
        }

        @PostMapping("/queue/{operation}")
        public void produceMessagess(@PathVariable("operation") String operation) throws InterruptedException, KeeperException {
               // produce(operation);
                queueTest(operation);
        }

        @PostMapping("/lock")
        public void lock(@RequestBody String[] args) {
            Locks.exec(args);
        }
}
