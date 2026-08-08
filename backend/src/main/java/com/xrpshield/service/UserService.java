package com.xrpshield.service;

import com.xrpshield.dto.UserRequestDto;
import com.xrpshield.dto.UserResponseDto;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.entity.UserStatus;
import com.xrpshield.exception.ResourceNotFoundException;
import com.xrpshield.mapper.UserMapper;
import com.xrpshield.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDto createUser(UserRequestDto request) {
        logger.info("Creating user with email: {}", request.getEmail());
        UserEntity entity = userMapper.toEntityFromRequest(request);
        UserEntity saved = userRepository.save(entity);
        return userMapper.toDto(saved);
    }

    public UserResponseDto getUserById(UUID id) {
        logger.debug("Fetching user by ID: {}", id);
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDto(entity);
    }

    public List<UserResponseDto> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUserStatus(UUID id, UserStatus status) {
        logger.info("Updating user {} status to {}", id, status);
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        entity.setStatus(status);
        UserEntity updated = userRepository.save(entity);
        return userMapper.toDto(updated);
    }
}
