package com.vorofpie.timetracker.dto.request;

public record TaskDetailRequest(
        String name,
        String description,
        String status
) {}