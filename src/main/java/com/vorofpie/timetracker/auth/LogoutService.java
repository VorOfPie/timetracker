package com.vorofpie.timetracker.auth;

import com.vorofpie.timetracker.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling user logout.
 * Implements Spring Security's LogoutHandler interface to process logout requests.
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    // Token repository dependency to manage user tokens
    private final TokenRepository tokenRepository;

    /**
     * Handles user logout by invalidating the user's JWT token.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param authentication the authentication object representing the user's current authentication
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Extract JWT token from the authorization header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // If no valid JWT token found, return without further action
        }
        jwt = authHeader.substring(7); // Extract the token excluding "Bearer "

        // Retrieve the stored token from the repository based on the JWT
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        // If the token is found in the repository
        if (storedToken != null) {
            // Mark the token as expired and revoked
            storedToken.setExpired(true);
            storedToken.setRevoked(true);

            // Save the updated token state back to the repository
            tokenRepository.save(storedToken);

            // Clear the security context to ensure the user is logged out
            SecurityContextHolder.clearContext();
        }
    }
}
