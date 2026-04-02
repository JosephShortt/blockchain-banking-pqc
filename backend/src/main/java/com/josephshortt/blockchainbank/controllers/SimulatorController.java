package com.josephshortt.blockchainbank.controllers;

import com.josephshortt.blockchainbank.TransactionSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulate")
public class SimulatorController {

    @Autowired
    private TransactionSimulator transactionSimulator;

    @PostMapping("/start")
    public ResponseEntity<?> start() {
        transactionSimulator.start();
        return ResponseEntity.ok("Simulation started");
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stop() {
        transactionSimulator.stop();
        return ResponseEntity.ok("Simulation stopped");
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(transactionSimulator.isRunning() ? "running" : "stopped");
    }
}