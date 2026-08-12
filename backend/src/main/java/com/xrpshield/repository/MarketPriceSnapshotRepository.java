package com.xrpshield.repository;

import com.xrpshield.entity.MarketPriceSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPriceSnapshotRepository extends JpaRepository<MarketPriceSnapshotEntity, String> {
    List<MarketPriceSnapshotEntity> findBySymbolOrderByFeedTimestampDesc(String symbol);
    Optional<MarketPriceSnapshotEntity> findFirstBySymbolOrderByFeedTimestampDesc(String symbol);
}
