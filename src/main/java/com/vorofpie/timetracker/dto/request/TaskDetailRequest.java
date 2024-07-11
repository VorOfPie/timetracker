package com.vorofpie.timetracker.dto.request;

import com.vorofpie.timetracker.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskDetailRequest(
        @NotBlank(message = "{taskdetail.name.notblank}")
        @Size(max = 100, message = "{taskdetail.name.size}")
        String name,

        @Size(max = 255, message = "{taskdetail.description.size}")
        String description,

        @NotNull(message = "{taskdetail.status.notblank}")
        TaskStatus status,

        @NotNull(message = "{taskdetail.projectId.notblank}")
        Long projectId
) {}
