package com.vorofpie.timetracker.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RecordDetailRequest(
        @NotNull(message = "{recorddetail.starttime.notnull}")
        LocalDateTime startTime,

        @NotNull(message = "{recorddetail.endtime.notnull}")
        LocalDateTime endTime,

        @NotBlank(message = "{recorddetail.description.notblank}")
        String description
) {}
