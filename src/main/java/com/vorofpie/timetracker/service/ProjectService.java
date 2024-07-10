package com.vorofpie.timetracker.service;

import com.vorofpie.timetracker.dto.request.ProjectRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    List<ProjectResponse> getAllProjects();

    ProjectResponse getProjectById(Long id);

    ProjectResponse createProject(ProjectRequest projectRequest);

    ProjectResponse updateProject(Long id, ProjectRequest projectRequest);

    void deleteProject(Long id);
}
