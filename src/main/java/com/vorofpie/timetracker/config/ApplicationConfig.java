package com.vorofpie.timetracker.config;

import com.vorofpie.timetracker.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Configuration class for setting up beans related to security and validation.
 * This class is responsible for configuring security-related beans such as
 * authentication provider, password encoder, and validation settings.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    // Custom user details service used for authentication
    private final CustomUserDetailsService userDetailsService;

    /**
     * Configures a message source for validation messages.
     *
     * @return the configured MessageSource bean
     */
    @Bean
    public MessageSource messageSource() {
        // Create a new instance of ReloadableResourceBundleMessageSource
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Set the base name of the resource bundle for message sources
        messageSource.setBasename("classpath:messages");
        // Set the default encoding for the message source
        messageSource.setDefaultEncoding("UTF-8");
        // Return the configured message source
        return messageSource;
    }

    /**
     * Configures the LocalValidatorFactoryBean used for validating bean constraints.
     *
     * @return the configured LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean getValidator() {
        // Create a new instance of LocalValidatorFactoryBean
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        // Set the message source for validation messages
        bean.setValidationMessageSource(messageSource());
        // Return the configured validator factory bean
        return bean;
    }

    /**
     * Configures the UserDetailsService bean used for loading user-specific data.
     *
     * @return the CustomUserDetailsService bean
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    /**
     * Configures the AuthenticationProvider bean for authentication management.
     * Uses DaoAuthenticationProvider to authenticate users against a database.
     *
     * @return the configured AuthenticationProvider bean
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Create a new instance of DaoAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Set the UserDetailsService used for loading user-specific data
        authProvider.setUserDetailsService(userDetailsService());
        // Set the PasswordEncoder used for encoding passwords
        authProvider.setPasswordEncoder(passwordEncoder());
        // Return the configured authentication provider
        return authProvider;
    }

    /**
     * Configures the AuthenticationManager bean used for managing authentication.
     *
     * @param config the AuthenticationConfiguration used to obtain the AuthenticationManager
     * @return the configured AuthenticationManager bean
     * @throws Exception if an error occurs while creating the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Retrieve and return the AuthenticationManager from the provided AuthenticationConfiguration
        return config.getAuthenticationManager();
    }

    /**
     * Configures the PasswordEncoder bean used for encoding and verifying passwords.
     * Uses BCryptPasswordEncoder for secure password hashing.
     *
     * @return the configured PasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Return a new instance of BCryptPasswordEncoder for secure password encoding
        return new BCryptPasswordEncoder();
    }
}
