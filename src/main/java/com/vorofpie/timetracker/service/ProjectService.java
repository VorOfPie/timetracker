package com.vorofpie.timetracker.service;

import com.vorofpie.timetracker.dto.request.CreateProjectRequest;
import com.vorofpie.timetracker.dto.request.UpdateProjectRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    List<ProjectResponse> getAllProjects();

    ProjectResponse getProjectById(Long id);

    ProjectResponse createProject(CreateProjectRequest createProjectRequest);

    ProjectResponse updateProject(Long id, UpdateProjectRequest createProjectRequest);

    void deleteProject(Long id);

    ProjectResponse addUserToProject(Long projectId, Long userId);
}
