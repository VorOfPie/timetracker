package com.vorofpie.timetracker.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "task_details")
@Schema(description = "Task detail entity representing tasks within a project")
public class TaskDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Unique identifier for the task", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @Schema(description = "Project associated with this task", example = "1")
    private Project project;

    @Column(nullable = false)
    @Schema(description = "Name of the task", example = "Design Phase")
    private String name;

    @Schema(description = "Description of the task", example = "Design the initial project phases")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Status of the task", example = "IN_PROGRESS")
    private TaskStatus status;

    @OneToMany(mappedBy = "task")
    @Schema(description = "List of records associated with this task")
    private List<RecordDetail> recordDetails = new ArrayList<>();
}
