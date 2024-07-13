package com.vorofpie.timetracker.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vorofpie.timetracker.config.SecurityUser;
import com.vorofpie.timetracker.domain.Role;
import com.vorofpie.timetracker.domain.RoleName;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.UserRequest;
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

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(UserRequest request) {
        checkUserExistence(request.email());
        Role defaultRole = findRoleByName(RoleName.USER);
        UserDetails userDetails = buildUser(request, defaultRole);

        User savedUser = userRepository.save(((SecurityUser) userDetails).user());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        saveUserToken(savedUser, accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = findUserByEmail(request.email());
        UserDetails userDetails = new SecurityUser(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        final AuthenticationResponse authResponse;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUserEmail(refreshToken);

        if (userEmail != null) {
            User user = findUserByEmail(userEmail);
            UserDetails userDetails = new SecurityUser(user);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);
                revokeAllUserTokens(user);
                saveUserToken(user, newAccessToken);

                authResponse = AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();

                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private void saveUserToken(User user, String accessToken) {
        Token token = Token.builder()
                .user(user)
                .token(accessToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

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

    private Role findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName).get();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).get();}

    private void checkUserExistence(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("This user is already exists");
        }
    }

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
