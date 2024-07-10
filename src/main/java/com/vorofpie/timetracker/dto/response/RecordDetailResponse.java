package com.vorofpie.timetracker.dto.response;

import java.time.LocalDateTime;


public record RecordDetailResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description
) {}
