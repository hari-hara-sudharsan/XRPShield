package com.xrpshield.blockchain;

import com.xrpshield.entity.ContractMetadataEntity;
import com.xrpshield.repository.ContractMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService {

    private static final Logger logger = LoggerFactory.getLogger(ContractService.class);

    private final ContractMetadataRepository contractMetadataRepository;
    private final BlockchainConfiguration config;

    public ContractService(ContractMetadataRepository contractMetadataRepository, BlockchainConfiguration config) {
        this.contractMetadataRepository = contractMetadataRepository;
        this.config = config;
    }

    public List<ContractMetadataEntity> getDeployedContracts() {
        return contractMetadataRepository.findAll();
    }

    public ContractMetadataEntity registerDeployedContract(String name, String address, String abi, Long blockNumber) {
        logger.info("Registering deployed contract metadata for {} at {}", name, address);
        ContractMetadataEntity entity = contractMetadataRepository.findByContractAddress(address)
                .orElseGet(ContractMetadataEntity::new);

        entity.setContractName(name);
        entity.setContractAddress(address);
        entity.setAbiJson(abi);
        entity.setNetworkName("Flare Coston2");
        entity.setChainId(config.getChainId());
        entity.setDeployedBlock(blockNumber);
        entity.setStatus("ACTIVE");

        return contractMetadataRepository.save(entity);
    }
}
