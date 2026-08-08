package com.xrpshield.repository;

import com.xrpshield.entity.ContractMetadataEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractMetadataRepository extends BaseRepository<ContractMetadataEntity> {

    Optional<ContractMetadataEntity> findByContractAddress(String contractAddress);

    Optional<ContractMetadataEntity> findByContractName(String contractName);
}
