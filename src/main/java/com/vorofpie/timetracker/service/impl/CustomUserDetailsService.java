package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.config.SecurityUser;
import com.vorofpie.timetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of the UserDetailsService interface to load user-specific data during authentication.
 * This service fetches user details from the database and wraps them into a SecurityUser object.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // Repository to interact with the User entity in the database
    private final UserRepository userRepository;

    /**
     * Loads user-specific data by username (email) from the database.
     * This method is used by Spring Security during the authentication process to fetch user details.
     *
     * @param username the email of the user to look up
     * @return a UserDetails object containing user information
     * @throws UsernameNotFoundException if the user with the given username (email) is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user by email using the UserRepository
        // Wrap the found user into a SecurityUser object
        return new SecurityUser(
                userRepository.findByEmail(username)
                        // Throw exception if user is not found
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("Username not found: %s", username)))
        );
    }
}
