package com.vorofpie.timetracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.vorofpie.timetracker.domain.TaskStatus;
import java.util.List;

@Schema(description = "Task Detail Response DTO")
public record TaskDetailResponse(
        @Schema(description = "Unique identifier for the task", example = "1")
        Long id,

        @Schema(description = "Name of the task", example = "Design Phase")
        String name,

        @Schema(description = "Description of the task", example = "Design the initial project phases")
        String description,

        @Schema(description = "Status of the task", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "List of records associated with this task")
        List<RecordDetailResponse> recordDetails
) {}
