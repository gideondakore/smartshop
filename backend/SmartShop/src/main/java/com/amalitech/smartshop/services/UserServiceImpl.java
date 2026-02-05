package com.amalitech.smartshop.services;

import com.amalitech.smartshop.cache.CacheManager;
import com.amalitech.smartshop.dtos.requests.LoginDTO;
import com.amalitech.smartshop.dtos.requests.UpdateUserDTO;
import com.amalitech.smartshop.dtos.requests.UserRegistrationDTO;
import com.amalitech.smartshop.dtos.responses.LoginResponseDTO;
import com.amalitech.smartshop.dtos.responses.UserSummaryDTO;
import com.amalitech.smartshop.entities.Order;
import com.amalitech.smartshop.entities.User;
import com.amalitech.smartshop.exceptions.ResourceAlreadyExistsException;
import com.amalitech.smartshop.exceptions.ResourceNotFoundException;
import com.amalitech.smartshop.interfaces.OrderItemRepository;
import com.amalitech.smartshop.interfaces.OrderRepository;
import com.amalitech.smartshop.interfaces.UserRepository;
import com.amalitech.smartshop.interfaces.UserService;
import com.amalitech.smartshop.mappers.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the UserService interface.
 * Handles all user-related business logic including registration,
 * authentication, and user management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheManager cacheManager;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public LoginResponseDTO addUser(UserRegistrationDTO userDTO) {
        log.info("Registering new user with email: {}", userDTO.getEmail());
        
        userRepository.findByEmail(userDTO.getEmail())
                .ifPresent(user -> {
                    throw new ResourceAlreadyExistsException("Email already exists: " + userDTO.getEmail());
                });

        User user = userMapper.toEntity(userDTO);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        User savedUser = userRepository.save(user);
        LoginResponseDTO responseDTO = userMapper.toResponseDTO(savedUser);
        responseDTO.setToken(generateToken(savedUser.getId()));
        
        log.info("User registered successfully with id: {}", savedUser.getId());
        return responseDTO;
    }

    @Override
    public LoginResponseDTO loginUser(LoginDTO loginDTO) {
        log.info("Attempting login for email: {}", loginDTO.getEmail());
        
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginDTO.getEmail()));

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        LoginResponseDTO responseDTO = userMapper.toResponseDTO(user);
        responseDTO.setToken(generateToken(user.getId()));
        
        log.info("User logged in successfully: {}", user.getId());
        return responseDTO;
    }

    @Override
    public UserSummaryDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        UserSummaryDTO summary = userMapper.toSummaryDTO(user);
        summary.setName(user.getFullName());
        return summary;
    }

    @Override
    public UserSummaryDTO updateUser(Long id, @Valid UpdateUserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        parseAndSetName(userDTO);

        String oldEmail = user.getEmail();
        userMapper.updateEntity(userDTO, user);
        User updatedUser = userRepository.save(user);

        invalidateUserCache(id, oldEmail, userDTO.getEmail());

        UserSummaryDTO summary = userMapper.toSummaryDTO(updatedUser);
        summary.setName(updatedUser.getFullName());
        return summary;
    }

    @Override
    public Page<UserSummaryDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user ->
                cacheManager.get("user:" + user.getId(), () -> {
                    UserSummaryDTO summary = userMapper.toSummaryDTO(user);
                    summary.setName(user.getFullName());
                    return summary;
                })
        );
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        String email = user.getEmail();

        // Delete associated orders and order items
        List<Order> orders = orderRepository.findByUserId(id);
        for (Order order : orders) {
            orderItemRepository.deleteAll(orderItemRepository.findByOrderId(order.getId()));
            orderRepository.delete(order);
            cacheManager.invalidate("order:" + order.getId());
        }

        userRepository.delete(user);
        cacheManager.invalidate("user:" + id);
        cacheManager.invalidate("user:email:" + email);
        
        log.info("User deleted successfully: {}", id);
    }

    private String generateToken(Long userId) {
        String randomString = UUID.randomUUID().toString().replace("-", "");
        return randomString + "-" + userId;
    }

    private void parseAndSetName(UpdateUserDTO userDTO) {
        if (userDTO.getName() != null && !userDTO.getName().trim().isEmpty()) {
            String[] nameParts = userDTO.getName().trim().split("\\s+", 2);
            userDTO.setFirstName(nameParts[0]);
            userDTO.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        }
    }

    private void invalidateUserCache(Long id, String oldEmail, String newEmail) {
        cacheManager.invalidate("user:" + id);
        cacheManager.invalidate("user:email:" + oldEmail);
        if (newEmail != null && !oldEmail.equals(newEmail)) {
            cacheManager.invalidate("user:email:" + newEmail);
        }
    }
}
