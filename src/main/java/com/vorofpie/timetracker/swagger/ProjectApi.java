package com.vorofpie.timetracker.swagger;

import com.vorofpie.timetracker.dto.error.AppError;
import com.vorofpie.timetracker.dto.request.CreateProjectRequest;
import com.vorofpie.timetracker.dto.request.UpdateProjectRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Project API", description = "Project management API")
public interface ProjectApi {

    @Operation(summary = "Create a new project", description = "Create a new project. Ony ADMIN can access this endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    ProjectResponse createProject(@RequestBody CreateProjectRequest createProjectRequest);

    @Operation(summary = "Update an existing project by ID", description = "Update a project by its ID. Only users in the project group and ADMIN can access this endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    ProjectResponse updateProject(@PathVariable Long id, @RequestBody UpdateProjectRequest updateProjectRequest);

    @Operation(summary = "Delete a project by ID", description = "Delete a project by its ID. Ony ADMIN can access this endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    void deleteProject(@PathVariable Long id);

    @Operation(summary = "Retrieve a project by ID", description = "Get a project by its ID. Only users in the project group and ADMIN can access this endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    ProjectResponse getProjectById(@PathVariable Long id);

    @Operation(summary = "Retrieve all projects", description = "Get a list of all projects. ADMIN gets all projects, users only get the projects they are members of")
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class)))
    List<ProjectResponse> getAllProjects();

    @Operation(summary = "Add user to project", description = "Add a user to a project by project ID and user ID. Ony ADMIN can access this endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User added to project successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project or user not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    ProjectResponse addUserToProject(@PathVariable Long projectId, @PathVariable Long userId);
}
