package com.xrpshield.service;

import com.xrpshield.audit.VaultAuditService;
import com.xrpshield.dto.*;
import com.xrpshield.entity.*;
import com.xrpshield.security.Role;
import com.xrpshield.mapper.VaultMapper;
import com.xrpshield.repository.*;
import com.xrpshield.validator.VaultValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class VaultServiceTest {

    private VaultRepository vaultRepository;
    private UserRepository userRepository;
    private VaultBalanceRepository vaultBalanceRepository;
    private VaultTransactionRepository vaultTransactionRepository;
    private VaultHistoryRepository vaultHistoryRepository;
    private VaultMapper vaultMapper;
    private VaultValidator vaultValidator;
    private VaultAuditService vaultAuditService;
    private VaultService vaultService;

    @BeforeEach
    void setUp() {
        vaultRepository = Mockito.mock(VaultRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        vaultBalanceRepository = Mockito.mock(VaultBalanceRepository.class);
        vaultTransactionRepository = Mockito.mock(VaultTransactionRepository.class);
        vaultHistoryRepository = Mockito.mock(VaultHistoryRepository.class);
        vaultMapper = new VaultMapper();
        vaultValidator = new VaultValidator();
        vaultAuditService = new VaultAuditService(vaultHistoryRepository);

        vaultService = new VaultService(
                vaultRepository, userRepository, vaultBalanceRepository,
                vaultTransactionRepository, vaultHistoryRepository,
                vaultMapper, vaultValidator, vaultAuditService
        );
    }

    @Test
    @DisplayName("Should create confidential treasury vault cleanly")
    void testCreateVault() {
        UserEntity user = new UserEntity("owner@xrpshield.io", "Owner Name", "hashedPass", Role.ROLE_USER, UserStatus.ACTIVE);
        user.setId(UUID.randomUUID());

        CreateVaultRequestDto request = new CreateVaultRequestDto();
        request.setName("Primary FXRP Treasury");
        request.setAssetType("FXRP");

        when(vaultRepository.save(any(VaultEntity.class))).thenAnswer(inv -> {
            VaultEntity v = inv.getArgument(0);
            v.setId(UUID.randomUUID());
            return v;
        });

        when(vaultRepository.findById(any(UUID.class))).thenAnswer(inv -> {
            UUID id = inv.getArgument(0);
            VaultEntity v = new VaultEntity(user, "Primary FXRP Treasury", "0x123", "FXRP", BigDecimal.ZERO, VaultStatus.ACTIVE);
            v.setId(id);
            return Optional.of(v);
        });

        VaultDetailsDto result = vaultService.createVault(request, user);

        assertNotNull(result);
        assertEquals("Primary FXRP Treasury", result.getName());
        assertEquals("FXRP", result.getCurrency());
    }
}
