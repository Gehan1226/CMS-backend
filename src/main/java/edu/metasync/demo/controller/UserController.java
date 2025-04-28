package edu.metasync.demo.controller;

import edu.metasync.demo.dto.auth.AccessToken;
import edu.metasync.demo.dto.auth.UserCreateRequest;
import edu.metasync.demo.dto.auth.UserLoginRequest;
import edu.metasync.demo.dto.response.SuccessResponse;
import edu.metasync.demo.dto.response.SuccessResponseWithData;
import edu.metasync.demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public SuccessResponse addUser(@Valid @RequestBody UserCreateRequest user, BindingResult result) {
        userService.addUser(user);
        return SuccessResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("User register successfully !")
                .build();
    }

    @PostMapping("/login")
    public SuccessResponseWithData<AccessToken> login(@Valid @RequestBody UserLoginRequest userLoginRequest,
                                                      BindingResult result, HttpServletResponse response) {
        AccessToken token = userService.authenticateAndGenerateToken(userLoginRequest);
        return SuccessResponseWithData.<AccessToken>builder()
                .status(HttpStatus.OK.value())
                .message("User logged in successfully !")
                .data(token)
                .build();
    }

}
