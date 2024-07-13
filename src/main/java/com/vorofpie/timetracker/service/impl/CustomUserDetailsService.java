package com.vorofpie.timetracker.service.impl;


import com.vorofpie.timetracker.config.SecurityUser;
import com.vorofpie.timetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUser(
                userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("Username not found", username)))
        );
    }
}
