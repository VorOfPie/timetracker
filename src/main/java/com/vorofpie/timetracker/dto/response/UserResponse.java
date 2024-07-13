package com.vorofpie.timetracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "User Response DTO")
public record UserResponse(
        @Schema(description = "Unique identifier for the user", example = "1")
        Long id,

        @Schema(description = "Username of the user", example = "john_doe")
        String username,

        @Schema(description = "Email of the user", example = "john.doe@example.com")
        String email,

        @Schema(description = "Birth date of the user", example = "1990-01-01")
        LocalDate birthDate
) {}
