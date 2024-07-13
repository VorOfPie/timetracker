package com.vorofpie.timetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "Create Project Request DTO")
public record CreateProjectRequest(
        @NotBlank(message = "{project.name.notblank}")
        @Size(max = 100, message = "{project.name.size}")
        @Schema(description = "Name of the project", example = "Project Alpha")
        String name,

        @Size(max = 255, message = "{project.description.size}")
        @Schema(description = "Description of the project", example = "A description of the project")
        String description,

        @Schema(description = "List of task details associated with the project")
        List<TaskDetailRequest> taskDetails,

        @Schema(description = "List of users associated with the project")
        List<UserRequest> users
) {}
