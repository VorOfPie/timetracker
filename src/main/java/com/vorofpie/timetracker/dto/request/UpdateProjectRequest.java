package com.vorofpie.timetracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @NotBlank(message = "{project.name.notblank}")
        @Size(max = 100, message = "{project.name.size}")
        String name,

        @Size(max = 255, message = "{project.description.size}")
        String description
) {
}
