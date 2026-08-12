package com.xrpshield.controller;

import com.xrpshield.service.PrivacyProofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/proof")
@CrossOrigin(origins = "*")
public class ProofController {

    @Autowired
    private PrivacyProofService privacyProofService;

    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestProof() {
        return ResponseEntity.ok(privacyProofService.getPrivacyProof("latest"));
    }

    @GetMapping("/vault/{id}")
    public ResponseEntity<Map<String, Object>> getProofByVault(@PathVariable("id") String vaultId) {
        return ResponseEntity.ok(privacyProofService.getPrivacyProof(vaultId));
    }
}
