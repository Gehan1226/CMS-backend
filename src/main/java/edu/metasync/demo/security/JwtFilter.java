package edu.metasync.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metasync.demo.dto.response.ErrorResponse;
import edu.metasync.demo.service.impl.MyUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromCookies(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String userName = extractUserNameFromToken(token, response);
        if (userName == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        authenticateUser(token, userName, request);
        filterChain.doFilter(request, response);
    }


    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractUserNameFromToken(String token, HttpServletResponse response) throws IOException {
        try {
            return jwtService.extractUserName(token);
        } catch (ExpiredJwtException e) {
            handleJwtError(response, "Unauthorized: JWT has expired.");
        } catch (MalformedJwtException e) {
            handleJwtError(response, "Unauthorized: Malformed JWT.");
        } catch (Exception e) {
            handleJwtError(response, "Unauthorized: Invalid JWT token.");
        }
        return null;
    }

    private void authenticateUser(String token, String userName, HttpServletRequest request) {
        UserDetails userDetails = applicationContext.getBean(MyUserDetailService.class)
                .loadUserByUsername(userName);

        if (jwtService.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }


    private void handleJwtError(HttpServletResponse response, String errorMessage) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .errorMessage(errorMessage)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
