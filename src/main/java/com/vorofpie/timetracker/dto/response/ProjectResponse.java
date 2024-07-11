package com.vorofpie.timetracker.dto.response;

import java.util.List;


public record ProjectResponse(
        Long id,
        String name,
        String description,
        List<TaskDetailResponse> taskDetails,
        List<UserResponse> users
) {
}