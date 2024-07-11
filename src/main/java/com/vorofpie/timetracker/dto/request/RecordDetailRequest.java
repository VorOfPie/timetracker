package com.vorofpie.timetracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RecordDetailRequest(
        @NotNull(message = "{recorddetail.starttime.notnull}")
        LocalDateTime startTime,

        @NotNull(message = "{recorddetail.endtime.notnull}")
        LocalDateTime endTime,

        @NotBlank(message = "{recorddetail.description.notblank}")
        String description,

        @NotNull(message = "{recorddetail.taskid.notnull}")
        Long taskId,
        @NotBlank(message = "{user.email.notblank}")
        @Email(message = "{user.email.invalid}")
        String userEmail
) {
}
