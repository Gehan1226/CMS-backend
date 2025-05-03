package edu.metasync.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metasync.demo.dto.auth.*;
import edu.metasync.demo.entity.RefreshTokenEntity;
import edu.metasync.demo.entity.UserEntity;
import edu.metasync.demo.exception.DataDuplicateException;
import edu.metasync.demo.exception.DataNotFoundException;
import edu.metasync.demo.exception.UnauthorizedException;
import edu.metasync.demo.exception.UnexpectedException;
import edu.metasync.demo.repository.RefreshTokenRepository;
import edu.metasync.demo.repository.UserRepository;
import edu.metasync.demo.security.JWTService;
import edu.metasync.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final RefreshTokenRepository refreshTokenRepo;

    @Override
    public void addUser(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByUserName(userCreateRequest.getUserName())) {
            throw new DataDuplicateException(
                    "User with username " + userCreateRequest.getUserName() + " already exists");
        }
        userCreateRequest.setPassword(encoder.encode(userCreateRequest.getPassword()));

        try {
            UserEntity userEntity = objectMapper.convertValue(userCreateRequest, UserEntity.class);
            userRepository.save(userEntity);
        } catch (Exception exception) {
            throw new UnexpectedException("An unexpected error occurred while saving the user");
        }
    }

    @Override
    public AuthResponse authenticateAndGenerateToken(UserLoginRequest userLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getUserName(), userLoginRequest.getPassword()));
        RefreshTokenEntity refreshToken = createRefreshToken(userLoginRequest.getUserName());
        return new AuthResponse(
                jwtService.generateToken(userLoginRequest.getUserName()),
                refreshToken.getToken());
    }

    @Override
    public UserResponse getUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName == null) {
            throw new DataNotFoundException("User not found in the security context");
        }

        try {
            UserEntity userEntity = userRepository.findByUserName(userName);
            if (userEntity == null) {
                throw new DataNotFoundException("User with username " + userName + " not found");
            }
            return objectMapper.convertValue(userEntity, UserResponse.class);
        } catch (Exception exception) {
            throw new UnexpectedException("An unexpected error occurred while retrieving the user");
        }
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        RefreshTokenEntity token = refreshTokenRepo.findByToken(refreshToken);
        if (token == null) {
            throw new DataNotFoundException("Refresh token is invalid");
        }

        if (token.isExpired()) {
            refreshTokenRepo.delete(token);
            throw new UnauthorizedException("Refresh token is expired");
        }
        String accessToken = jwtService.generateToken(token.getUser().getUserName());
        return new AuthResponse(accessToken, token.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        RefreshTokenEntity token = refreshTokenRepo.findByToken(refreshToken);
        if (token == null) {
            throw new DataNotFoundException("Refresh token is invalid");
        }
        try {
            refreshTokenRepo.delete(token);
        } catch (Exception exception) {
            throw new UnexpectedException("An unexpected error occurred while deleting the refresh token");
        }
    }

    public RefreshTokenEntity createRefreshToken(String userName) {
        try {
            UserEntity user = userRepository.findByUserName(userName);
            RefreshTokenEntity token = new RefreshTokenEntity();
            token.setUser(user);
            token.setToken(UUID.randomUUID().toString());
            token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
            return refreshTokenRepo.save(token);
        } catch (Exception exception) {
            throw new UnexpectedException("An unexpected error occurred while creating the refresh token");
        }
    }
}