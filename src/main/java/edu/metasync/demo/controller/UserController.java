package edu.metasync.demo.controller;

import edu.metasync.demo.dto.auth.*;
import edu.metasync.demo.dto.response.SuccessResponse;
import edu.metasync.demo.dto.response.SuccessResponseWithData;
import edu.metasync.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;
    private static final  String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final  String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final  String REFRESH_TOKEN_PATH = "/";

    @PostMapping()
    public SuccessResponse addUser(@Valid @RequestBody UserCreateRequest user, BindingResult result) {
        userService.addUser(user);
        return SuccessResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("User register successfully !")
                .build();
    }

    @PostMapping("/login")
    public SuccessResponse login(@Valid @RequestBody UserLoginRequest userLoginRequest,
                                                      BindingResult result, HttpServletResponse response) {
        AuthResponse authResponse = userService.authenticateAndGenerateToken(userLoginRequest);

        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, authResponse.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(7 * 24 * 60 * 60);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, authResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath(REFRESH_TOKEN_PATH);
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return SuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("User logged in successfully !")
                .build();
    }

    @GetMapping()
    public SuccessResponseWithData<UserResponse> getUser() {
        UserResponse user = userService.getUser();
        return SuccessResponseWithData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully !")
                .data(user)
                .build();
    }

    @PostMapping("/refresh")
    public SuccessResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookie(request, REFRESH_TOKEN_COOKIE_NAME);

        System.out.println("Refresh token: " + refreshToken);

        if (refreshToken == null || refreshToken.isEmpty()) {
            return SuccessResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Refresh token is missing or invalid")
                    .build();
        }

        AuthResponse authResponse = userService.refresh(refreshToken);
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, authResponse.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, authResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath(REFRESH_TOKEN_PATH);
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        return SuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Refresh token generated successfully !")
                .build();
    }

    @PostMapping("/logout")
    public SuccessResponse logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookie(request, REFRESH_TOKEN_COOKIE_NAME);

        if (refreshToken == null || refreshToken.isEmpty()) {
            return SuccessResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Refresh token is missing or invalid")
                    .build();
        }

        userService.logout(refreshToken);

        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath(REFRESH_TOKEN_PATH);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return SuccessResponse.builder()
                .status(HttpStatus.OK.value())
                .message("User logged out successfully !")
                .build();
    }

    private String extractTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
