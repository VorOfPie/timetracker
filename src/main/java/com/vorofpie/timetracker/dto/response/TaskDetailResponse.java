package com.vorofpie.timetracker.dto.response;



public record TaskDetailResponse(
        Long id,
        String name,
        String description,
        String status
) {}
