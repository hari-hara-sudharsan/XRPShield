package com.xrpshield.repository;

import com.xrpshield.entity.UserEntity;
import com.xrpshield.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByStatus(UserStatus status);

    boolean existsByEmail(String email);
}
