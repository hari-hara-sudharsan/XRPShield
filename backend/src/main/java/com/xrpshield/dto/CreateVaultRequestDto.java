package com.xrpshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateVaultRequestDto {

    @NotBlank(message = "Vault name is required")
    @Size(min = 3, max = 100, message = "Vault name must be between 3 and 100 characters")
    private String name;

    private String description;

    @NotBlank(message = "Asset type is required")
    private String assetType = "FXRP";

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
