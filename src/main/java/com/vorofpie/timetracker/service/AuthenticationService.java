package com.vorofpie.timetracker.service;


import com.vorofpie.timetracker.auth.AuthenticationRequest;
import com.vorofpie.timetracker.auth.AuthenticationResponse;
import com.vorofpie.timetracker.dto.request.UserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(UserRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
