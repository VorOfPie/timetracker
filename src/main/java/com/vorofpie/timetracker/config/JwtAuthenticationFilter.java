package com.vorofpie.timetracker.config;

import com.vorofpie.timetracker.service.JwtService;
import com.vorofpie.timetracker.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom filter for processing JWT authentication in incoming HTTP requests.
 * This filter extracts JWT from the request header, validates it, and sets the authentication in the security context.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Service for handling JWT operations such as token validation and extraction
    private final JwtService jwtService;
    // Service for loading user-specific data based on username (email in this case)
    private final UserDetailsService userDetailsService;
    // Repository for managing tokens, used to check if the token is revoked or expired
    private final TokenRepository tokenRepository;

    /**
     * Filters incoming requests to perform JWT authentication.
     * Extracts the JWT from the request, validates it, and sets the authentication context if the token is valid.
     *
     * @param request   the HTTP request
     * @param response  the HTTP response
     * @param filterChain the filter chain to pass the request and response to the next filter
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip filtering for authentication endpoints to avoid interference with login requests
        if (request.getServletPath().equals("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Retrieve the Authorization header from the request
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwtToken;
        final String userEmail;

        // Proceed with the filter chain if the Authorization header is missing or does not start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token from the header by removing the "Bearer " prefix
        jwtToken = authHeader.substring(7);
        // Extract the user email from the JWT token
        userEmail = jwtService.extractUserEmail(jwtToken);

        // If user email is extracted and no authentication is set in the security context
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from the UserDetailsService
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            // Check if the token is valid (not expired or revoked)
            boolean isTokenValid = tokenRepository.findByToken(jwtToken)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            // If the token is valid according to the JwtService and token repository
            if (jwtService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
                // Create an authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set the details for the authentication token (e.g., request details)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set the authentication token in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continue the filter chain processing
        filterChain.doFilter(request, response);
    }
}
