package com.vorofpie.timetracker.dto.request;


import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record RecordDetailRequest(
        @NotBlank(message = "{recorddetail.starttime.notnull}")
        LocalDateTime startTime,

        @NotBlank(message = "{recorddetail.endtime.notnull}")
        LocalDateTime endTime,

        @NotBlank(message = "{recorddetail.description.notblank}")
        String description
) {}
