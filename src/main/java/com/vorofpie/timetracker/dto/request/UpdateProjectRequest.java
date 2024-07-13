package com.vorofpie.timetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Update Project Request DTO")
public record UpdateProjectRequest(
        @NotBlank(message = "{project.name.notblank}")
        @Size(max = 100, message = "{project.name.size}")
        @Schema(description = "Name of the project", example = "Updated Project Alpha")
        String name,

        @Size(max = 255, message = "{project.description.size}")
        @Schema(description = "Description of the project", example = "Updated description of the project")
        String description
) {}
