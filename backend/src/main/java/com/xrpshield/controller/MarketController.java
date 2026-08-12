package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.dto.MarketPriceResponseDto;
import com.xrpshield.service.FtsoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Market & Price Oracles", description = "Endpoints for live Flare FTSOv2 decentralized oracle feeds")
public class MarketController {

    private final FtsoService ftsoService;

    public MarketController(FtsoService ftsoService) {
        this.ftsoService = ftsoService;
    }

    @GetMapping({"/api/market/xrp-usd", "/api/v1/market/xrp-usd"})
    @Operation(summary = "Get Real XRP/USD FTSOv2 Market Price", description = "Queries live Flare FTSOv2 decentralized oracle contract resolved via Flare Registry for real XRP/USD feed")
    public ResponseEntity<ApiResponse<MarketPriceResponseDto>> getXrpUsdPrice() {
        MarketPriceResponseDto dto = ftsoService.fetchLiveXRPUSDPrice();
        return ResponseEntity.ok(ApiResponse.success("Live FTSOv2 XRP/USD market price fetched successfully", dto));
    }
}
