package com.vorofpie.timetracker.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateRecordDetailRequest(
        @NotBlank(message = "{recorddetail.starttime.notblank}")
        LocalDateTime startTime,

        @NotBlank(message = "{recorddetail.endtime.notblank}")
        LocalDateTime endTime,

        @NotBlank(message = "{recorddetail.description.notblank}")
        String description,

        @NotBlank(message = "{recorddetail.taskid.notblank}")
        Long taskId
) {
}
