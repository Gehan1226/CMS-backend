package edu.metasync.demo.service;

import edu.metasync.demo.dto.auth.*;


public interface UserService {
    void addUser(UserCreateRequest userCreateRequest);

    AuthResponse authenticateAndGenerateToken(UserLoginRequest userLoginRequest);

    UserResponse getUser();

    AuthResponse refresh(String refreshToken);

    void logout(String refreshToken);
}