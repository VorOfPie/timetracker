package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.aspect.annotation.ProjectMemberAccess;
import com.vorofpie.timetracker.config.SecurityUser;
import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.CreateProjectRequest;
import com.vorofpie.timetracker.dto.request.UpdateProjectRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import com.vorofpie.timetracker.mapper.ProjectMapper;
import com.vorofpie.timetracker.repository.ProjectRepository;
import com.vorofpie.timetracker.repository.UserRepository;
import com.vorofpie.timetracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vorofpie.timetracker.domain.RoleName.ADMIN;
import static com.vorofpie.timetracker.error.ErrorMessages.*;

/**
 * Implementation of the ProjectService interface, providing project management operations.
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository; // Repository for project data access
    private final ProjectMapper projectMapper; // Mapper for converting between entities and DTOs
    private final UserRepository userRepository; // Repository for user data access

    /**
     * Retrieves all projects. If the user is an admin, all projects are returned; otherwise, only projects associated with the user are returned.
     *
     * @return a list of project responses
     */
    @Override
    public List<ProjectResponse> getAllProjects() {
        // Get the current authenticated user
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user has admin role
        boolean isAdmin = securityUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + ADMIN.name()));

        List<Project> projects;
        // If the user is admin, fetch all projects; otherwise, fetch projects associated with the user
        if (isAdmin) {
            projects = projectRepository.findAll();
        } else {
            projects = projectRepository.findByUsers_Id(securityUser.user().getId());
        }

        // Map the list of projects to a list of project responses
        return projects.stream()
                .map(projectMapper::toProjectResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a project by its ID. Requires that the user has access to the project.
     *
     * @param id the ID of the project
     * @return the project response
     */
    @Override
    @ProjectMemberAccess
    public ProjectResponse getProjectById(Long id) {
        // Find the project by ID or throw an exception if not found
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Project", id)));

        // Convert the project entity to a project response DTO
        return projectMapper.toProjectResponse(project);
    }

    /**
     * Creates a new project with the provided details.
     *
     * @param createProjectRequest the details of the project to create
     * @return the created project response
     */
    @Override
    public ProjectResponse createProject(CreateProjectRequest createProjectRequest) {
        // Map the request DTO to a project entity
        Project project = projectMapper.toProject(createProjectRequest);

        // Set the relationships between tasks and records if they exist
        Optional.ofNullable(project.getTaskDetails())
                .ifPresent(tasks -> tasks.forEach(task -> {
                    task.setProject(project);
                    Optional.ofNullable(task.getRecordDetails())
                            .ifPresent(records -> records.forEach(record -> record.setTask(task)));
                }));

        // Save the project and convert it to a project response DTO
        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    /**
     * Updates an existing project with the provided details.
     *
     * @param id the ID of the project to update
     * @param updateProjectRequest the new details for the project
     * @return the updated project response
     */
    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest updateProjectRequest) {
        // Find the existing project or throw an exception if not found
        Project existingProject = findProjectByIdOrThrow(id);

        // Update the project entity with the new details
        projectMapper.updateProjectFromProjectRequest(updateProjectRequest, existingProject);

        // Save the updated project and convert it to a project response DTO
        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toProjectResponse(updatedProject);
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id the ID of the project to delete
     */
    @Override
    public void deleteProject(Long id) {
        // Delete the project by ID
        projectRepository.deleteById(id);
    }

    /**
     * Adds a user to a project.
     *
     * @param projectId the ID of the project
     * @param userId the ID of the user to add
     * @return the updated project response
     */
    @Override
    public ProjectResponse addUserToProject(Long projectId, Long userId) {
        // Find the project or throw an exception if not found
        Project project = findProjectByIdOrThrow(projectId);

        // Find the user or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId)));

        // Add the user to the project's user list
        project.getUsers().add(user);

        // Save the updated project and convert it to a project response DTO
        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    /**
     * Finds a project by its ID or throws an exception if not found.
     *
     * @param id the ID of the project
     * @return the project entity
     * @throws ResourceNotFoundException if the project is not found
     */
    private Project findProjectByIdOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Project", id)));
    }
}
