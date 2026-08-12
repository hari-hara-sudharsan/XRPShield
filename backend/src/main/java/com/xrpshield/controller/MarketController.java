package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.dto.MarketPriceResponseDto;
import com.xrpshield.service.FtsoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final FtsoService ftsoService;

    public MarketController(FtsoService ftsoService) {
        this.ftsoService = ftsoService;
    }

    @GetMapping("/xrp-usd")
    public ResponseEntity<ApiResponse<MarketPriceResponseDto>> getXrpUsdPrice() {
        MarketPriceResponseDto dto = ftsoService.fetchLiveXRPUSDPrice();
        return ResponseEntity.ok(ApiResponse.success("Live FTSOv2 XRP/USD market price fetched successfully", dto));
    }
}
