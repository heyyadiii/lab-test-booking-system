package com.labtest.service;

import com.labtest.config.JwtTokenProvider;
import com.labtest.dto.AuthResponse;
import com.labtest.dto.LoginRequest;
import com.labtest.dto.RegisterRequest;
import com.labtest.entity.User;
import com.labtest.exception.ConflictException;
import com.labtest.exception.ResourceNotFoundException;
import com.labtest.exception.ValidationException;
import com.labtest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        
        // Security: Prevent users from registering as ADMIN
        // Only PATIENT role allowed during registration
        if (request.getRole() != null && request.getRole() != com.labtest.entity.UserRole.PATIENT) {
            throw new ValidationException("Invalid role. Only PATIENT role is allowed during registration");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt encryption
        user.setRole(com.labtest.entity.UserRole.PATIENT); // Force PATIENT role
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        
        userRepository.save(user);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());
        
        return new AuthResponse(token, user.getUsername(), user.getRole().toString());
    }
    
    public AuthResponse authenticateUser(LoginRequest request) {
        // Authenticate user - BCrypt comparison happens here internally
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Get user details
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return new AuthResponse(token, user.getUsername(), user.getRole().toString());
    }
}
