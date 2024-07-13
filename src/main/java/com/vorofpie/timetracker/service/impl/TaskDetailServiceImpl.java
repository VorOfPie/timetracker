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

@Service
@RequiredArgsConstructor
public class TaskDetailServiceImpl implements TaskDetailService {

    private final TaskDetailRepository taskDetailRepository;
    private final TaskDetailMapper taskDetailMapper;
    private final ProjectRepository projectRepository;

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

    @ProjectMemberAccess
    @Override
    public TaskDetailResponse getTaskDetailById(Long id) {
        TaskDetail taskDetail = findTaskDetailByIdOrThrow(id);
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    @ProjectMemberAccess
    @Override
    public TaskDetailResponse createTaskDetail(TaskDetailRequest taskDetailRequest) {
        Project project = findProjectByIdOrThrow(taskDetailRequest.projectId());
        TaskDetail taskDetail = taskDetailMapper.toTaskDetail(taskDetailRequest);
        taskDetail.setProject(project);
        taskDetail = taskDetailRepository.save(taskDetail);
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    @ProjectMemberAccess
    @Override
    public TaskDetailResponse updateTaskDetail(Long id, TaskDetailRequest taskDetailRequest) {
        TaskDetail existingTaskDetail = findTaskDetailByIdOrThrow(id);
        Project project = findProjectByIdOrThrow(taskDetailRequest.projectId());

        validateStatusTransition(existingTaskDetail.getStatus(), taskDetailRequest.status());

        taskDetailMapper.updateTaskDetailFromRequest(taskDetailRequest, existingTaskDetail);
        existingTaskDetail.setProject(project);
        existingTaskDetail = taskDetailRepository.save(existingTaskDetail);
        return taskDetailMapper.toTaskDetailResponse(existingTaskDetail);
    }

    @ProjectMemberAccess
    @Override
    public void deleteTaskDetail(Long id) {
        taskDetailRepository.deleteById(id);
    }

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

    private TaskDetail findTaskDetailByIdOrThrow(Long id) {
        return taskDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Task detail", id)));
    }

    private Project findProjectByIdOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Project", projectId)));
    }
}
