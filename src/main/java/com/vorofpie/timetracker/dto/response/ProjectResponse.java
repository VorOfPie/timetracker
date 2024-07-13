package com.vorofpie.timetracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Project Response DTO")
public record ProjectResponse(
        @Schema(description = "Unique identifier for the project", example = "1")
        Long id,

        @Schema(description = "Name of the project", example = "Project Alpha")
        String name,

        @Schema(description = "Description of the project", example = "A description of the project")
        String description,

        @Schema(description = "List of task details associated with the project")
        List<TaskDetailResponse> taskDetails,

        @Schema(description = "List of users associated with the project")
        List<UserResponse> users
) {}
