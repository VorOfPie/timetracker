package com.vorofpie.timetracker.dto.response;

import java.time.LocalDateTime;
import java.util.List;


public record RecordDetailResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description
) {}
