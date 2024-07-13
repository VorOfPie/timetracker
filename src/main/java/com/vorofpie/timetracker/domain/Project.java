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
@Table(name = "projects")
@Schema(description = "Project entity representing a project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Unique identifier for the project", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Name of the project", example = "Project Alpha")
    private String name;

    @Schema(description = "Description of the project", example = "A description of the project")
    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "List of task details associated with the project")
    private List<TaskDetail> taskDetails = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Schema(description = "List of users associated with the project")
    private List<User> users = new ArrayList<>();
}
