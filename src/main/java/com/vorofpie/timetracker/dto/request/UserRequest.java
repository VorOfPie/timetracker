package com.vorofpie.timetracker.dto.request;


import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRequest(
        @NotBlank(message = "{user.username.notblank}")
        @Size(max = 50, message = "{user.username.size}")
        String username,

        @NotBlank(message = "{user.password.notblank}")
        @Size(min = 6, message = "{user.password.size}")
        String password,

        @NotBlank(message = "{user.email.notblank}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotBlank(message = "{user.birthdate.notblank}")
        @Past(message = "{user.birthdate.past}")
        LocalDate birthDate
) {}
