package com.xrpshield.repository;

import com.xrpshield.entity.GeneratedReportEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GeneratedReportRepository extends BaseRepository<GeneratedReportEntity> {

    List<GeneratedReportEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
