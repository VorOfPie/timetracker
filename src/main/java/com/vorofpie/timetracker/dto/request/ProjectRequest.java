package com.vorofpie.timetracker.dto.request;

import java.util.List;

public record ProjectRequest(
        String name,
        String description,
        List<RecordDetailRequest> recordDetails,
        List<TaskDetailRequest> taskDetails
){}