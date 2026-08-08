package com.xrpshield.service;

import com.xrpshield.dto.SystemConfigurationDto;
import com.xrpshield.entity.SystemConfigurationEntity;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.SystemConfigurationMapper;
import com.xrpshield.repository.SystemConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigurationService.class);

    private final SystemConfigurationRepository systemConfigurationRepository;
    private final SystemConfigurationMapper systemConfigurationMapper;

    public SystemConfigurationService(SystemConfigurationRepository systemConfigurationRepository, SystemConfigurationMapper systemConfigurationMapper) {
        this.systemConfigurationRepository = systemConfigurationRepository;
        this.systemConfigurationMapper = systemConfigurationMapper;
    }

    public SystemConfigurationDto setConfiguration(SystemConfigurationDto dto) {
        logger.info("Setting system configuration key: {}", dto.getConfigKey());
        SystemConfigurationEntity entity = systemConfigurationRepository.findByConfigKey(dto.getConfigKey())
                .orElseGet(SystemConfigurationEntity::new);

        entity.setConfigKey(dto.getConfigKey());
        entity.setConfigValue(dto.getConfigValue());
        entity.setDescription(dto.getDescription());

        SystemConfigurationEntity saved = systemConfigurationRepository.save(entity);
        return systemConfigurationMapper.toDto(saved);
    }

    public SystemConfigurationDto getConfigurationByKey(String configKey) {
        SystemConfigurationEntity entity = systemConfigurationRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new ResourceNotFoundException("SystemConfiguration", "configKey", configKey));
        return systemConfigurationMapper.toDto(entity);
    }

    public List<SystemConfigurationDto> getAllConfigurations() {
        return systemConfigurationRepository.findAll().stream()
                .map(systemConfigurationMapper::toDto)
                .collect(Collectors.toList());
    }
}
