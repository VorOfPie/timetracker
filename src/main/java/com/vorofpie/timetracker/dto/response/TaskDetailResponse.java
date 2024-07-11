package com.vorofpie.timetracker.dto.response;

import com.vorofpie.timetracker.domain.TaskStatus;

import java.util.List;

public record TaskDetailResponse(
        Long id,
        String name,
        String description,
        TaskStatus status,
        List<RecordDetailResponse> recordDetails
) {}
