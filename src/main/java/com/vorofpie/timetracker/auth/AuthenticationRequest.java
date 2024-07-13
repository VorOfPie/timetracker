package com.vorofpie.timetracker.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(

        @Email(message = "{user.email.valid}")
        @NotBlank(message = "{user.email.notnull}")
        String email,

        @NotNull(message = "{user.password.notnull}")
        String password

) {
}
