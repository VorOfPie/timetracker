package com.vorofpie.timetracker.swagger;

import com.vorofpie.timetracker.dto.error.AppError;
import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.dto.response.TaskDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Task Detail API", description = "Task detail management API")
public interface TaskDetailApi {

    @Operation(summary = "Create a new task detail", description = "Create a new task detail. A regular user can only create a task in a project he/she is a member of. An administrator can create a task for any project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task detail created successfully", content = @Content(schema = @Schema(implementation = TaskDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    TaskDetailResponse createTaskDetail(@RequestBody TaskDetailRequest taskDetailRequest);

    @Operation(summary = "Update an existing task detail by ID", description = "Update a task detail by its ID. A regular user can only update a task in a project he/she is a member of. An administrator can update a task for any project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task detail updated successfully", content = @Content(schema = @Schema(implementation = TaskDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "404", description = "Task detail not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    TaskDetailResponse updateTaskDetail(@PathVariable Long id, @RequestBody TaskDetailRequest taskDetailRequest);

    @Operation(summary = "Delete a task detail by ID", description = "Delete a task detail by its ID. A regular user can only delete a task in a project he/she is a member of. An administrator can delete a task for any project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task detail deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task detail not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    void deleteTaskDetail(@PathVariable Long id);

    @Operation(summary = "Retrieve a task detail by ID", description = "Get a task detail by its ID. Only users in the project group can get record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task detail retrieved successfully", content = @Content(schema = @Schema(implementation = TaskDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task detail not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    TaskDetailResponse getTaskDetailById(@PathVariable Long id);

    @Operation(summary = "Retrieve all task details", description = "Get a list of all task details. Regular user gets all tasks from project he/she participates in.")
    @ApiResponse(responseCode = "200", description = "Task details retrieved successfully", content = @Content(schema = @Schema(implementation = TaskDetailResponse.class)))
    List<TaskDetailResponse> getAllTaskDetails();
}
