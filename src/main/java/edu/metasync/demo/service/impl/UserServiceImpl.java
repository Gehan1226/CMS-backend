package edu.metasync.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metasync.demo.dto.auth.AccessToken;
import edu.metasync.demo.dto.auth.UserCreateRequest;
import edu.metasync.demo.dto.auth.UserLoginRequest;
import edu.metasync.demo.dto.auth.UserResponse;
import edu.metasync.demo.entity.UserEntity;
import edu.metasync.demo.exception.DataDuplicateException;
import edu.metasync.demo.exception.DataNotFoundException;
import edu.metasync.demo.exception.UnexpectedException;
import edu.metasync.demo.repository.UserRepository;
import edu.metasync.demo.security.JWTService;
import edu.metasync.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

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
    public AccessToken authenticateAndGenerateToken(UserLoginRequest userLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getUserName(), userLoginRequest.getPassword()));

        return new AccessToken(jwtService.generateToken(userLoginRequest.getUserName()));
    }

    @Override
    public UserResponse getUser(String userName) {
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
}