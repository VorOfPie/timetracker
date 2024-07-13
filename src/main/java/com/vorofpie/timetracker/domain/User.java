package com.vorofpie.timetracker.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
@Schema(description = "User entity representing system users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Unique identifier for the user", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Column(nullable = false)
    @Schema(description = "Password of the user", example = "password123")
    private String password;

    @Column(unique = true, nullable = false)
    @Schema(description = "Email of the user", example = "john.doe@example.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @Schema(description = "Role assigned to the user", example = "1")
    private Role role;
}
