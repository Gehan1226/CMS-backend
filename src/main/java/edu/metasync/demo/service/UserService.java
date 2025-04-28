package edu.metasync.demo.service;

import edu.metasync.demo.dto.auth.AccessToken;
import edu.metasync.demo.dto.auth.UserCreateRequest;
import edu.metasync.demo.dto.auth.UserLoginRequest;


public interface UserService {
    void addUser(UserCreateRequest userCreateRequest);

    AccessToken authenticateAndGenerateToken(UserLoginRequest userLoginRequest);

}