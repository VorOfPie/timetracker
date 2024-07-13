package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.aspect.annotation.ProjectMemberAccess;
import com.vorofpie.timetracker.config.SecurityUser;
import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.domain.TaskStatus;
import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.dto.response.TaskDetailResponse;
import com.vorofpie.timetracker.error.exception.InvalidStatusTransitionException;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import com.vorofpie.timetracker.mapper.TaskDetailMapper;
import com.vorofpie.timetracker.repository.ProjectRepository;
import com.vorofpie.timetracker.repository.TaskDetailRepository;
import com.vorofpie.timetracker.service.TaskDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vorofpie.timetracker.domain.RoleName.ADMIN;
import static com.vorofpie.timetracker.error.ErrorMessages.*;

/**
 * Service implementation for managing task details.
 * <p>
 * This service provides methods to manage task details, including retrieving, creating, updating, and deleting task details.
 * Access to these operations is controlled based on the user's role and project membership.
 *
 * @see ProjectMemberAccess
 */
@Service
@RequiredArgsConstructor
public class TaskDetailServiceImpl implements TaskDetailService {

    private final TaskDetailRepository taskDetailRepository;
    private final TaskDetailMapper taskDetailMapper;
    private final ProjectRepository projectRepository;

    /**
     * Retrieves all task details.
     * <p>
     * If the user is an admin, all task details are returned.
     * Otherwise, only task details associated with the user's projects are returned.
     *
     * @return a list of task detail responses
     */
    @Override
    public List<TaskDetailResponse> getAllTaskDetails() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = securityUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + ADMIN.name()));

        List<TaskDetail> taskDetails;
        if (isAdmin) {
            taskDetails = taskDetailRepository.findAll();
        } else {
            taskDetails = taskDetailRepository.findByProject_Users_Id(securityUser.user().getId());
        }

        return taskDetails.stream()
                .map(taskDetailMapper::toTaskDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a task detail by its ID.
     * <p>
     * This method is annotated with {@link ProjectMemberAccess} to ensure that only authorized project members can access the task detail.
     *
     * @param id the ID of the task detail to retrieve
     * @return the task detail response
     * @throws ResourceNotFoundException if the task detail is not found
     */
    @ProjectMemberAccess
    @Override
    public TaskDetailResponse getTaskDetailById(Long id) {
        TaskDetail taskDetail = findTaskDetailByIdOrThrow(id);
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    /**
     * Creates a new task detail.
     * <p>
     * This method is annotated with {@link ProjectMemberAccess} to ensure that only authorized project members can create a task detail.
     *
     * @param taskDetailRequest the request object containing details of the task to be created
     * @return the created task detail response
     * @throws ResourceNotFoundException if the associated project is not found
     */
    @ProjectMemberAccess
    @Override
    public TaskDetailResponse createTaskDetail(TaskDetailRequest taskDetailRequest) {
        Project project = findProjectByIdOrThrow(taskDetailRequest.projectId());
        TaskDetail taskDetail = taskDetailMapper.toTaskDetail(taskDetailRequest);
        taskDetail.setProject(project);
        taskDetail = taskDetailRepository.save(taskDetail);
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    /**
     * Updates an existing task detail.
     * <p>
     * This method is annotated with {@link ProjectMemberAccess} to ensure that only authorized project members can update a task detail.
     *
     * @param id                the ID of the task detail to update
     * @param taskDetailRequest the request object containing updated details of the task
     * @return the updated task detail response
     * @throws ResourceNotFoundException        if the task detail or the associated project is not found
     * @throws InvalidStatusTransitionException if the status transition is invalid
     */
    @ProjectMemberAccess
    @Override
    public TaskDetailResponse updateTaskDetail(Long id, TaskDetailRequest taskDetailRequest) {
        TaskDetail existingTaskDetail = findTaskDetailByIdOrThrow(id);
        Project project = findProjectByIdOrThrow(taskDetailRequest.projectId());

        // Validate status transition rules
        validateStatusTransition(existingTaskDetail.getStatus(), taskDetailRequest.status());

        taskDetailMapper.updateTaskDetailFromRequest(taskDetailRequest, existingTaskDetail);
        existingTaskDetail.setProject(project);
        existingTaskDetail = taskDetailRepository.save(existingTaskDetail);
        return taskDetailMapper.toTaskDetailResponse(existingTaskDetail);
    }

    /**
     * Deletes a task detail by its ID.
     * <p>
     * This method is annotated with {@link ProjectMemberAccess} to ensure that only authorized project members can delete a task detail.
     *
     * @param id the ID of the task detail to delete
     */
    @ProjectMemberAccess
    @Override
    public void deleteTaskDetail(Long id) {
        taskDetailRepository.deleteById(id);
    }

    /**
     * Validates the transition between task statuses.
     * <p>
     * Ensures that the status transition is valid according to predefined rules.
     *
     * @param currentStatus the current status of the task
     * @param newStatus     the new status to transition to
     * @throws InvalidStatusTransitionException if the status transition is invalid
     */
    private void validateStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        if (currentStatus == TaskStatus.COMPLETED || currentStatus == TaskStatus.ON_HOLD) {
            throw new InvalidStatusTransitionException(COMPLETED_OR_CANCELLED_STATUS_MESSAGE);
        }
        if (currentStatus == TaskStatus.OPEN && newStatus != TaskStatus.IN_PROGRESS) {
            throw new InvalidStatusTransitionException(FROM_OPEN_TO_IN_PROGRESS_STATUS_MESSAGE);
        }
        if (currentStatus == TaskStatus.IN_PROGRESS && (newStatus != TaskStatus.COMPLETED && newStatus != TaskStatus.ON_HOLD)) {
            throw new InvalidStatusTransitionException(FROM_IN_PROGRESS_TO_COMPLETED_OR_ON_HOLD_STATUS_MESSAGE);
        }
    }

    /**
     * Finds a task detail by its ID or throws an exception if not found.
     *
     * @param id the ID of the task detail
     * @return the task detail
     * @throws ResourceNotFoundException if the task detail is not found
     */
    private TaskDetail findTaskDetailByIdOrThrow(Long id) {
        return taskDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Task detail", id)));
    }

    /**
     * Finds a project by its ID or throws an exception if not found.
     *
     * @param projectId the ID of the project
     * @return the project
     * @throws ResourceNotFoundException if the project is not found
     */
    private Project findProjectByIdOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Project", projectId)));
    }
}
