package com.vorofpie.timetracker.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateProjectRequest(
        @NotBlank(message = "{project.name.notblank}")
        @Size(max = 100, message = "{project.name.size}")
        String name,

        @Size(max = 255, message = "{project.description.size}")
        String description,

        List<TaskDetailRequest> taskDetails,

        List<UserRequest> users
) {}
