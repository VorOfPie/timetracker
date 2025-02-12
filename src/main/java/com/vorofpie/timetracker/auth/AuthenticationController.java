package com.vorofpie.timetracker.auth;


import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.service.AuthenticationService;
import com.vorofpie.timetracker.swagger.AuthenticationApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationApi{

    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public AuthenticationResponse register(
            @RequestBody @Valid UserRequest request
    ) {
        return service.register(request);
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public AuthenticationResponse authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return service.authenticate(request);
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

}
