package com.vorofpie.timetracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Record Detail Response DTO")
public record RecordDetailResponse(
        @Schema(description = "Unique identifier for the record", example = "1")
        Long id,

        @Schema(description = "Start time of the record", example = "2024-07-13T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "End time of the record", example = "2024-07-13T12:00:00")
        LocalDateTime endTime,

        @Schema(description = "Description of the record", example = "Worked on task XYZ")
        String description
) {}
