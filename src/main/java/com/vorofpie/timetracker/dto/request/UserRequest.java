package com.vorofpie.timetracker.dto.request;

import java.time.LocalDate;


public record UserRequest(
        String username,
        String password,
        String email,
        LocalDate birthDate
){}
