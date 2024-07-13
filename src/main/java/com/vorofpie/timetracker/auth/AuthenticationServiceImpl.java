package com.vorofpie.timetracker.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vorofpie.timetracker.config.SecurityUser;
import com.vorofpie.timetracker.domain.Role;
import com.vorofpie.timetracker.domain.RoleName;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.error.ErrorMessages;
import com.vorofpie.timetracker.error.exception.DuplicateResourceException;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import com.vorofpie.timetracker.repository.RoleRepository;
import com.vorofpie.timetracker.repository.UserRepository;
import com.vorofpie.timetracker.service.AuthenticationService;
import com.vorofpie.timetracker.service.JwtService;
import com.vorofpie.timetracker.token.Token;
import com.vorofpie.timetracker.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service implementation for handling user authentication and registration processes.
 * Utilizes JWT tokens for authentication and authorization.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    // Dependencies required for various authentication and authorization operations
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     * Checks if the user already exists, creates the user with a default role, and generates JWT tokens.
     *
     * @param request the user registration request containing user details
     * @return the authentication response with access and refresh tokens
     */
    @Override
    public AuthenticationResponse register(UserRequest request) {
        // Check if a user with the provided email already exists
        checkUserExistence(request.email());

        // Fetch the default user role
        Role defaultRole = findRoleByName(RoleName.USER);

        // Build the user entity from the registration request
        UserDetails userDetails = buildUser(request, defaultRole);

        // Save the user entity to the repository
        User savedUser = userRepository.save(((SecurityUser) userDetails).user());

        // Generate JWT tokens for the newly registered user
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Save the access token in the repository
        saveUserToken(savedUser, accessToken);

        // Return the authentication response containing the tokens
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Authenticates an existing user.
     * Validates the user's credentials and generates new JWT tokens.
     *
     * @param request the authentication request containing user credentials
     * @return the authentication response with access and refresh tokens
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate the user using the provided credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Fetch the user entity by email
        User user = findUserByEmail(request.email());

        // Wrap the user entity in a UserDetails implementation
        UserDetails userDetails = new SecurityUser(user);

        // Generate new JWT tokens for the authenticated user
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Revoke all existing tokens for the user
        revokeAllUserTokens(user);

        // Save the new access token in the repository
        saveUserToken(user, accessToken);

        // Return the authentication response containing the new tokens
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Refreshes JWT tokens.
     * Extracts the refresh token from the request, validates it, and generates new JWT tokens.
     *
     * @param request the HTTP request containing the refresh token
     * @param response the HTTP response to write the new tokens to
     * @throws IOException if an I/O error occurs during response writing
     */
    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        final AuthenticationResponse authResponse;

        // Check if the authorization header is present and contains a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        // Extract the refresh token from the authorization header
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUserEmail(refreshToken);

        // Validate the extracted user email and refresh token
        if (userEmail != null) {
            User user = findUserByEmail(userEmail);
            UserDetails userDetails = new SecurityUser(user);

            // Check if the refresh token is valid
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                // Generate new access and refresh tokens
                String newAccessToken = jwtService.generateToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);

                // Revoke all existing tokens for the user
                revokeAllUserTokens(user);

                // Save the new access token in the repository
                saveUserToken(user, newAccessToken);

                // Build the authentication response containing the new tokens
                authResponse = AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();

                // Write the authentication response to the HTTP response output stream
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    /**
     * Saves a user's token to the repository.
     * Marks the token as not expired and not revoked.
     *
     * @param user the user entity to associate the token with
     * @param accessToken the access token to save
     */
    private void saveUserToken(User user, String accessToken) {
        Token token = Token.builder()
                .user(user)
                .token(accessToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revokes all valid tokens for a user.
     * Marks the tokens as expired and revoked.
     *
     * @param user the user entity whose tokens to revoke
     */
    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Finds a role by its name.
     * Throws an exception if the role is not found.
     *
     * @param roleName the name of the role to find
     * @return the role entity
     */
    private Role findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND_MESSAGE, roleName)));
    }

    /**
     * Finds a user by their email.
     * Throws an exception if the user is not found.
     *
     * @param email the email of the user to find
     * @return the user entity
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_MESSAGE, email)));
    }

    /**
     * Checks if a user with the specified email already exists.
     * Throws an exception if the user exists.
     *
     * @param email the email to check for existence
     */
    private void checkUserExistence(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(String.format(ErrorMessages.DUPLICATE_RESOURCE_MESSAGE, "User", "email"));
        }
    }

    /**
     * Builds a user entity from a user registration request.
     * Encodes the user's password and sets their role.
     *
     * @param request the user registration request
     * @param role the role to assign to the user
     * @return the UserDetails implementation wrapping the user entity
     */
    private UserDetails buildUser(UserRequest request, Role role) {
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .birthDate(request.birthDate())
                .role(role)
                .build();

        return new SecurityUser(user);
    }
}
