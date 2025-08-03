package com.ufabc_next.sistema_matriculas.controller;

import org.springframework.web.bind.annotation.*;

import static com.ufabc_next.sistema_matriculas.core.leaderElection.LeaderElection.leaderElection;


@RestController
@RequestMapping("/leader")
public class DomainController {

        @PostMapping("/elect")
        public void electLeader(@RequestBody String[] args) {
            leaderElection(args);
        }
}
