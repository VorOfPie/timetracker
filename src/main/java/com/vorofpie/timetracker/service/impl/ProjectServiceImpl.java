package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.CreateProjectRequest;
import com.vorofpie.timetracker.dto.request.UpdateProjectRequest;
import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;
import com.vorofpie.timetracker.mapper.ProjectMapper;
import com.vorofpie.timetracker.repository.ProjectRepository;
import com.vorofpie.timetracker.repository.UserRepository;
import com.vorofpie.timetracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final UserRepository userRepository;

    @Override
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest createProjectRequest) {
        Project project = projectMapper.toProject(createProjectRequest);
        Optional.ofNullable(project.getTaskDetails())
                .ifPresent(tasks -> tasks.forEach(task -> {
                    task.setProject(project);
                    Optional.ofNullable(task.getRecordDetails())
                            .ifPresent(records -> records.forEach(record -> record.setTask(task)));
                }));
        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest updateProjectRequest) {
        Project existingProject = findProjectByIdOrThrow(id);
        projectMapper.updateProjectFromProjectRequest(updateProjectRequest, existingProject);
        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toProjectResponse(updatedProject);
    }


    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public ProjectResponse addUserToProject(Long projectId, Long userId) {
        Project project = findProjectByIdOrThrow(projectId);
        User user = userRepository.findById(userId).orElseThrow(() ->new IllegalArgumentException("Project not found") );
        project.getUsers().add(user);
        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    private Project findProjectByIdOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }
}
