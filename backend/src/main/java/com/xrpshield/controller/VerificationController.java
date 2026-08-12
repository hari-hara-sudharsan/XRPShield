package com.xrpshield.controller;

import com.xrpshield.service.TransactionVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationController {

    @Autowired
    private TransactionVerificationService transactionVerificationService;

    @GetMapping("/transaction/{hash}")
    public ResponseEntity<Map<String, Object>> verifyTransaction(@PathVariable("hash") String hash) {
        Map<String, Object> result = transactionVerificationService.verifyTransaction(hash);
        return ResponseEntity.ok(result);
    }
}
