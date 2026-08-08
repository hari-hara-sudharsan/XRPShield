package com.xrpshield.mapper;

import com.xrpshield.dto.TransactionResponseDto;
import com.xrpshield.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper implements EntityMapper<TransactionEntity, TransactionResponseDto> {

    @Override
    public TransactionResponseDto toDto(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TransactionResponseDto(
                entity.getId(),
                entity.getVault() != null ? entity.getVault().getId() : null,
                entity.getTxHash(),
                entity.getTransactionType() != null ? entity.getTransactionType().name() : null,
                entity.getAmount(),
                entity.getAsset(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getAttestationProof(),
                entity.getBlockNumber(),
                entity.getCreatedAt()
        );
    }

    @Override
    public TransactionEntity toEntity(TransactionResponseDto dto) {
        if (dto == null) {
            return null;
        }
        TransactionEntity entity = new TransactionEntity();
        entity.setId(dto.getId());
        entity.setTxHash(dto.getTxHash());
        entity.setAmount(dto.getAmount());
        entity.setAsset(dto.getAsset());
        entity.setAttestationProof(dto.getAttestationProof());
        entity.setBlockNumber(dto.getBlockNumber());
        return entity;
    }
}
