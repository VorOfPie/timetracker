package com.vorofpie.timetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "User Request DTO")
public record UserRequest(
        @NotBlank(message = "{user.username.notblank}")
        @Size(max = 50, message = "{user.username.size}")
        @Schema(description = "Username of the user", example = "john_doe")
        String username,

        @NotBlank(message = "{user.password.notblank}")
        @Size(min = 6, message = "{user.password.size}")
        @Schema(description = "Password of the user", example = "password123")
        String password,

        @NotBlank(message = "{user.email.notblank}")
        @Email(message = "{user.email.invalid}")
        @Schema(description = "Email of the user", example = "john.doe@example.com")
        String email,

        @NotNull(message = "{user.birthdate.null}")
        @Past(message = "{user.birthdate.past}")
        @Schema(description = "Birth date of the user", example = "1990-01-01")
        LocalDate birthDate
) {}
