package com.xrpshield.controller;

import com.xrpshield.service.DexQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/hedge")
@CrossOrigin(origins = "*")
public class HedgeQuoteController {

    @Autowired
    private DexQuoteService dexQuoteService;

    @GetMapping("/quote")
    public ResponseEntity<Map<String, Object>> getHedgeQuote(
            @RequestParam(name = "amountIn", defaultValue = "100") BigDecimal amountIn,
            @RequestParam(name = "maxSlippage", defaultValue = "0.50") BigDecimal maxSlippage,
            @RequestParam(name = "xrpPrice", required = false) BigDecimal xrpPrice) {

        Map<String, Object> quote = dexQuoteService.calculateQuote(amountIn, maxSlippage, xrpPrice);
        return ResponseEntity.ok(quote);
    }
}
