package com.xrpshield.repository;

import com.xrpshield.entity.WalletEntity;
import com.xrpshield.entity.WalletType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends BaseRepository<WalletEntity> {

    Optional<WalletEntity> findByAddress(String address);

    List<WalletEntity> findByUserId(UUID userId);

    List<WalletEntity> findByUserIdAndWalletType(UUID userId, WalletType walletType);

    boolean existsByAddress(String address);
}
