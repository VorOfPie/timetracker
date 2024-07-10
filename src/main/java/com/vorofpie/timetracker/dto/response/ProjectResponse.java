package com.vorofpie.timetracker.dto.response;

import java.util.List;


public record ProjectResponse(
        Long id,
        String name,
        String description,
        List<RecordDetailResponse> recordDetails,
        List<TaskDetailResponse> taskDetails) {
}