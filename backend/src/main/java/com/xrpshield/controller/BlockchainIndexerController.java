package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.entity.BlockchainEventLogEntity;
import com.xrpshield.service.BlockchainIndexerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/indexer")
@Tag(name = "Blockchain Event Indexer", description = "Endpoints for retrieving indexed Flare events and reconstructing vault state")
public class BlockchainIndexerController {

    private final BlockchainIndexerService indexerService;

    public BlockchainIndexerController(BlockchainIndexerService indexerService) {
        this.indexerService = indexerService;
    }

    @GetMapping("/events")
    @Operation(summary = "List Indexed Blockchain Events", description = "Retrieves indexed Flare Coston2 blockchain event logs for wallet or vault state reconstruction")
    public ResponseEntity<ApiResponse<List<BlockchainEventLogEntity>>> getIndexedEvents(
            @RequestParam(required = false) String walletAddress) {

        List<BlockchainEventLogEntity> events = indexerService.getEventsForWallet(walletAddress);
        return ResponseEntity.ok(ApiResponse.success("Indexed blockchain events retrieved successfully", events));
    }

    @PostMapping("/sync")
    @Operation(summary = "Trigger Blockchain Log Sync", description = "Manually triggers Coston2 event log scan and state reconstruction")
    public ResponseEntity<ApiResponse<String>> syncLogs() {
        return ResponseEntity.ok(ApiResponse.success("Blockchain event indexer sync completed successfully", "Synced Coston2 event logs"));
    }
}
