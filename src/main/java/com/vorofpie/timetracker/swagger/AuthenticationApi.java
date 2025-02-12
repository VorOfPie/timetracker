package com.vorofpie.timetracker.swagger;

import com.vorofpie.timetracker.auth.AuthenticationRequest;
import com.vorofpie.timetracker.auth.AuthenticationResponse;
import com.vorofpie.timetracker.dto.error.AppError;
import com.vorofpie.timetracker.dto.request.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Tag(name = "Authentication Controller", description = "Authentication API")
public interface AuthenticationApi {

    @Operation(summary = "Registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    AuthenticationResponse register(@RequestBody UserRequest request);

    @Operation(summary = "Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request);

    @Operation(summary = "Refresh Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

}

