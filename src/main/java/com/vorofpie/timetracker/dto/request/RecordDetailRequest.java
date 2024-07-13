package com.vorofpie.timetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Record Detail Request DTO")
public record RecordDetailRequest(
        @NotNull(message = "{recorddetail.starttime.notnull}")
        @Schema(description = "Start time of the record", example = "2024-07-13T10:00:00")
        LocalDateTime startTime,

        @NotNull(message = "{recorddetail.endtime.notnull}")
        @Schema(description = "End time of the record", example = "2024-07-13T12:00:00")
        LocalDateTime endTime,

        @NotBlank(message = "{recorddetail.description.notblank}")
        @Schema(description = "Description of the record", example = "Worked on task XYZ")
        String description,

        @NotNull(message = "{recorddetail.taskid.notnull}")
        @Schema(description = "Task ID associated with the record", example = "1")
        Long taskId,

        @NotBlank(message = "{user.email.notblank}")
        @Email(message = "{user.email.invalid}")
        @Schema(description = "Email of the user associated with the record", example = "john.doe@example.com")
        String userEmail
) {}
