package com.xrpshield.integration;

import com.xrpshield.entity.FeatureFlagEntity;
import com.xrpshield.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {

    private final FeatureFlagRepository featureFlagRepository;

    public ConfigurationService(FeatureFlagRepository featureFlagRepository) {
        this.featureFlagRepository = featureFlagRepository;
    }

    public Map<String, Boolean> getFeatureFlags() {
        return featureFlagRepository.findAll().stream()
                .collect(Collectors.toMap(FeatureFlagEntity::getFlagKey, FeatureFlagEntity::isEnabled));
    }

    public List<FeatureFlagEntity> getAllFeatureFlags() {
        return featureFlagRepository.findAll();
    }
}
