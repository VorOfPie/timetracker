package com.vorofpie.timetracker.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "record_details")
@Schema(description = "Record detail entity representing time entries")
public class RecordDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Unique identifier for the record detail", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "User associated with this record", example = "1")
    private User user;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "Start time of the record", example = "2024-07-13T10:00:00")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @Schema(description = "End time of the record", example = "2024-07-13T12:00:00")
    private LocalDateTime endTime;

    @Schema(description = "Description of the record", example = "Worked on task XYZ")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @Schema(description = "Task associated with this record", example = "1")
    private TaskDetail task;
}
