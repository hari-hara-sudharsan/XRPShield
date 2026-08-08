package com.xrpshield.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "generated_reports")
public class GeneratedReportEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vault_id")
    private VaultEntity vault;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    @Column(name = "report_content", nullable = false, columnDefinition = "TEXT")
    private String reportContent;

    public GeneratedReportEntity() {}

    public GeneratedReportEntity(UserEntity user, VaultEntity vault, String reportType, String reportContent) {
        this.user = user;
        this.vault = vault;
        this.reportType = reportType;
        this.reportContent = reportContent;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public VaultEntity getVault() {
        return vault;
    }

    public void setVault(VaultEntity vault) {
        this.vault = vault;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }
}
