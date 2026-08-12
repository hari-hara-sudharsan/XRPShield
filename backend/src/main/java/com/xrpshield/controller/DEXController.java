package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.dto.DEXQuoteDto;
import com.xrpshield.service.DEXService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/dex")
@Tag(name = "DEX Execution Engine", description = "Endpoints for real Coston2 DEX router trading quotes and FXRP -> USDT0 swaps")
public class DEXController {

    private final DEXService dexService;

    public DEXController(DEXService dexService) {
        this.dexService = dexService;
    }

    @GetMapping("/quote")
    @Operation(summary = "Get Coston2 DEX Swap Quote", description = "Calculates expected USDT0 output and minimum output based on live FTSOv2 price and slippage tolerance")
    public ResponseEntity<ApiResponse<DEXQuoteDto>> getQuote(
            @RequestParam(required = false, defaultValue = "100") BigDecimal amountIn,
            @RequestParam(required = false, defaultValue = "0.5") BigDecimal slippage) {

        DEXQuoteDto quote = dexService.getDEXQuote(amountIn, slippage);
        return ResponseEntity.ok(ApiResponse.success("Coston2 DEX quote generated successfully", quote));
    }
}
