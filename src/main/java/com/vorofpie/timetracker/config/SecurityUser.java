package com.vorofpie.timetracker.config;

import com.vorofpie.timetracker.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of UserDetails to represent user information for Spring Security.
 * This class wraps around a User domain object and provides the necessary methods for security.
 * It implements UserDetails, which is required for Spring Security's authentication process.
 */
public record SecurityUser(User user) implements UserDetails {

    /**
     * Returns the authorities granted to the user.
     * In this implementation, it creates a single authority based on the user's role.
     *
     * @return a collection of granted authorities for the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Create a list with a single authority based on the user's role
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));
    }

    /**
     * Returns the password of the user.
     *
     * @return the user's password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username of the user.
     * In this implementation, the email of the user is used as the username.
     *
     * @return the user's email (used as username)
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account is expired.
     * This implementation uses the default behavior provided by UserDetails.
     *
     * @return true if the account is not expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        // Uses the default implementation from UserDetails interface
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Indicates whether the user's account is locked.
     * This implementation uses the default behavior provided by UserDetails.
     *
     * @return true if the account is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        // Uses the default implementation from UserDetails interface
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Indicates whether the user's credentials are expired.
     * This implementation uses the default behavior provided by UserDetails.
     *
     * @return true if the credentials are not expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // Uses the default implementation from UserDetails interface
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Indicates whether the user is enabled.
     * This implementation uses the default behavior provided by UserDetails.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        // Uses the default implementation from UserDetails interface
        return UserDetails.super.isEnabled();
    }
}
