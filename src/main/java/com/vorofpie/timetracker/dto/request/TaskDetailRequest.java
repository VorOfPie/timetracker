package com.vorofpie.timetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.vorofpie.timetracker.domain.TaskStatus;

@Schema(description = "Task Detail Request DTO")
public record TaskDetailRequest(
        @NotBlank(message = "{taskdetail.name.notblank}")
        @Size(max = 100, message = "{taskdetail.name.size}")
        @Schema(description = "Name of the task", example = "Design Phase")
        String name,

        @Size(max = 255, message = "{taskdetail.description.size}")
        @Schema(description = "Description of the task", example = "Design the initial project phases")
        String description,

        @NotNull(message = "{taskdetail.status.notblank}")
        @Schema(description = "Status of the task", example = "IN_PROGRESS")
        TaskStatus status,

        @NotNull(message = "{taskdetail.projectId.notblank}")
        @Schema(description = "Project ID associated with the task", example = "1")
        Long projectId
) {}
