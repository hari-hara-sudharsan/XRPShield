package com.xrpshield.controller;

import com.xrpshield.service.DexQuoteService;
import com.xrpshield.service.ExecutionMonitoringService;
import com.xrpshield.service.ReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hedge")
@CrossOrigin(origins = "*")
public class HedgeQuoteController {

    @Autowired
    private DexQuoteService dexQuoteService;

    @Autowired
    private ExecutionMonitoringService executionMonitoringService;

    @Autowired
    private ReconciliationService reconciliationService;

    @GetMapping("/quote")
    public ResponseEntity<Map<String, Object>> getHedgeQuote(
            @RequestParam(name = "amountIn", defaultValue = "100") BigDecimal amountIn,
            @RequestParam(name = "maxSlippage", defaultValue = "0.50") BigDecimal maxSlippage,
            @RequestParam(name = "xrpPrice", required = false) BigDecimal xrpPrice) {

        Map<String, Object> quote = dexQuoteService.calculateQuote(amountIn, maxSlippage, xrpPrice);
        return ResponseEntity.ok(quote);
    }

    @GetMapping("/monitor")
    public ResponseEntity<List<ExecutionMonitoringService.ExecutionRecord>> getMonitoredExecutions() {
        return ResponseEntity.ok(executionMonitoringService.getAllExecutionRecords());
    }

    @GetMapping("/reconcile")
    public ResponseEntity<Map<String, Object>> reconcileExecutions() {
        return ResponseEntity.ok(reconciliationService.reconcileExecutions());
    }
}
