package com.vorofpie.timetracker.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskDetailRequest(
        @NotBlank(message = "{taskdetail.name.notblank}")
        @Size(max = 100, message = "{taskdetail.name.size}")
        String name,

        @Size(max = 255, message = "{taskdetail.description.size}")
        String description,

        @NotBlank(message = "{taskdetail.status.notblank}")
        @Size(max = 50, message = "{taskdetail.status.size}")
        String status
) {}
