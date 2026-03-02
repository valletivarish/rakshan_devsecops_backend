package com.rakshan.codereview.service;

import com.rakshan.codereview.dto.UserDto;
import com.rakshan.codereview.exception.ResourceNotFoundException;
import com.rakshan.codereview.model.User;
import com.rakshan.codereview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for user profile management operations.
 * Provides methods to retrieve, list, and delete user accounts.
 * Uses readOnly transactions for read operations to optimise database performance.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Retrieves all registered users as DTOs */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** Retrieves a single user by ID */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return mapToDto(user);
    }

    /** Deletes a user account by ID */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    /** Maps User entity to UserDto, excluding sensitive fields like password */
    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
