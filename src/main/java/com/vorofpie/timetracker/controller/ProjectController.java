package com.vorofpie.timetracker.controller;

import com.vorofpie.timetracker.dto.request.CreateProjectRequest;
import com.vorofpie.timetracker.dto.request.UpdateProjectRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;
import com.vorofpie.timetracker.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@RequestBody @Valid CreateProjectRequest createProjectRequest) {
        return projectService.createProject(createProjectRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponse updateProject(@PathVariable Long id, @RequestBody @Valid UpdateProjectRequest updateProjectRequest) {
        return projectService.updateProject(id, updateProjectRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping("/{projectId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponse addUserToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        return projectService.addUserToProject(projectId, userId);
    }
}
