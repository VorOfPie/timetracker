package com.vorofpie.timetracker.dto.response;

import java.time.LocalDate;


public record UserResponse(
        Long id,
        String username,
        String email,
        LocalDate birthDate
) {}
