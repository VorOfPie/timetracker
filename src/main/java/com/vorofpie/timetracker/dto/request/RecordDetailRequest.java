package com.vorofpie.timetracker.dto.request;

import java.time.LocalDateTime;


public record RecordDetailRequest(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description
){}