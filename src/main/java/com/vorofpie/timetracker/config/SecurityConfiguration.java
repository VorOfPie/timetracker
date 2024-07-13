package com.vorofpie.timetracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.vorofpie.timetracker.domain.RoleName.ADMIN;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Security configuration for setting up authentication, authorization, and Swagger documentation.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    // Custom JWT authentication filter
    private final JwtAuthenticationFilter jwtAuthFilter;
    // Authentication provider to handle authentication
    private final AuthenticationProvider authenticationProvider;
    // Logout handler to manage logout functionality
    private final LogoutHandler logoutHandler;

    /**
     * Configures OpenAPI documentation with security schemes and API info.
     *
     * @return OpenAPI configuration object
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("Time tracker API")
                        .description("Demo Spring Boot application")
                        .version("1.0")
                );
    }

    /**
     * Configures security settings including CORS, CSRF, authorization, and session management.
     *
     * @param http HttpSecurity configuration object
     * @return SecurityFilterChain object
     * @throws Exception if there is an error during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable Cross-Origin Resource Sharing (CORS) handling
                .cors(AbstractHttpConfigurer::disable)
                // Disable Cross-Site Request Forgery (CSRF) protection
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // Permit all requests to authentication endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Permit all requests to Swagger UI endpoints for API documentation
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        // Restrict POST, PUT, DELETE requests to /api/v1/projects/** to users with ADMIN role
                        .requestMatchers(POST, "/api/v1/projects/**").hasRole(ADMIN.name())
                        .requestMatchers(PUT, "/api/v1/projects/**").hasRole(ADMIN.name())
                        .requestMatchers(DELETE, "/api/v1/projects/**").hasRole(ADMIN.name())
                        // Restrict GET requests to /api/v1/users to users with ADMIN role
                        .requestMatchers(GET, "/api/v1/users").hasRole(ADMIN.name())
                        // Require authentication for all other requests
                        .anyRequest().authenticated()
                )
                // Set session management to stateless to ensure no session is created or used
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                // Set the custom authentication provider
                .authenticationProvider(authenticationProvider)
                // Add custom JWT filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Configure logout handling
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler) // Custom logout handler
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }
}
