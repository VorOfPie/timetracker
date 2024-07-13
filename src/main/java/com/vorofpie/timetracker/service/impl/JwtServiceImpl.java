package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.service.JwtService;
import com.vorofpie.timetracker.service.props.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of the JwtService interface for handling JWT (JSON Web Tokens) operations.
 * Provides methods for creating, validating, and extracting information from JWTs.
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    // Holds JWT properties such as secret key and expiration times
    private final JwtProperties jwtProperties;

    /**
     * Extracts the user's email from the JWT token.
     *
     * @param token the JWT token
     * @return the user's email
     */
    @Override
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver a function to resolve the desired claim
     * @param <T> the type of the claim
     * @return the extracted claim
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token using the provided UserDetails and default claims.
     *
     * @param userDetails the user details to include in the token
     * @return the generated JWT token
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtProperties.getAccess());
    }

    /**
     * Generates a JWT token with custom claims.
     *
     * @param claims custom claims to include in the token
     * @param userDetails the user details to include in the token
     * @return the generated JWT token
     */
    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtProperties.getAccess());
    }

    /**
     * Generates a refresh JWT token.
     *
     * @param userDetails the user details to include in the token
     * @return the generated refresh JWT token
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtProperties.getRefresh());
    }

    /**
     * Validates the JWT token by checking if it is not expired and if the token's user email matches the provided user details.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to compare against
     * @return true if the token is valid; false otherwise
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userEmail = extractUserEmail(token);
        return userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Builds a JWT token with the specified claims, user details, and expiration time.
     *
     * @param claims custom claims to include in the token
     * @param userDetails the user details to include in the token
     * @param expiration the token expiration time in milliseconds
     * @return the generated JWT token
     */
    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(claims) // Set custom claims
                .setSubject(userDetails.getUsername()) // Set the subject (username or email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the issued date
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Set the expiration date
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign the token with the secret key and algorithm
                .compact(); // Compact the token into a string
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Set the signing key
                .build()
                .parseClaimsJws(token) // Parse the token and extract claims
                .getBody();
    }

    /**
     * Retrieves the signing key for JWT operations.
     *
     * @return the key used for signing and verifying JWTs
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret()); // Decode the base64-encoded secret
        return Keys.hmacShaKeyFor(keyBytes); // Generate the HMAC key
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token
     * @return true if the token is expired; false otherwise
     */
    private boolean isTokenExpired(String token) {
        return parseTokenExpiration(token).before(new Date()); // Check if the token's expiration date is before the current date
    }

    /**
     * Parses the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    private Date parseTokenExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // Extract the expiration claim
    }
}
