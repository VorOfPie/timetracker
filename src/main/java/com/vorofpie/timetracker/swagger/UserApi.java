package com.vorofpie.timetracker.swagger;

import com.vorofpie.timetracker.dto.error.AppError;
import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "User API", description = "User management API")
public interface UserApi {

    @Operation(summary = "Update an existing user by ID", description = "Update a user by its ID. A regular user can only update their own user information. An administrator can update any user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest);

    @Operation(summary = "Delete a user by ID", description = "Delete a user by its ID. A regular user can only delete their own user account. An administrator can delete any user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    void deleteUser(@PathVariable Long id);

    @Operation(summary = "Retrieve a user by ID", description = "Get a user by its ID. A regular user can only retrieve their own user information. An administrator can retrieve any user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    UserResponse getUserById(@PathVariable Long id);

    @Operation(summary = "Retrieve all users", description = "Get a list of all users. Only an administrator can retrieve all users.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    List<UserResponse> getAllUsers();
}
