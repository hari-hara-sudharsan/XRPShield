package com.xrpshield.repository;

import com.xrpshield.entity.SystemMetricsSnapshotEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemMetricsSnapshotRepository extends BaseRepository<SystemMetricsSnapshotEntity> {

    List<SystemMetricsSnapshotEntity> findByCategoryOrderByCreatedAtDesc(String category);
}
