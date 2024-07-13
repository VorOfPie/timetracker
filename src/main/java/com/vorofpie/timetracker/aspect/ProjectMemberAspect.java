package com.vorofpie.timetracker.aspect;

import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.domain.RecordDetail;
import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.error.exception.AccessDeniedException;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import com.vorofpie.timetracker.repository.ProjectRepository;
import com.vorofpie.timetracker.repository.RecordDetailRepository;
import com.vorofpie.timetracker.repository.TaskDetailRepository;
import com.vorofpie.timetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.vorofpie.timetracker.error.ErrorMessages.*;

/**
 * Aspect for checking project membership before accessing or modifying task and record details.
 * Ensures that only users who are members of a project or administrators can perform certain actions.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ProjectMemberAspect {

    private final UserRepository userRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final ProjectRepository projectRepository;
    private final RecordDetailRepository recordDetailRepository;

    /**
     * Pointcut for methods annotated with @ProjectMemberAccess.
     * Targets methods that require project membership validation.
     */
    @Pointcut("@annotation(com.vorofpie.timetracker.aspect.annotation.ProjectMemberAccess)")
    public void projectMemberAccess() {
    }

    /**
     * Validates project membership before creating or updating a TaskDetail.
     * Ensures the user is a member of the project specified in the TaskDetailRequest.
     *
     * @param taskDetailRequest the task detail request containing the project ID
     */
    @Before("projectMemberAccess() && args(taskDetailRequest,..)")
    public void beforeCreateOrUpdateTaskDetail(TaskDetailRequest taskDetailRequest) {
        checkProjectMembership(taskDetailRequest.projectId());
    }

    /**
     * Validates project membership before creating or updating a RecordDetail.
     * Ensures the user is a member of the project associated with the TaskDetail of the RecordDetailRequest.
     *
     * @param recordDetailRequest the record detail request containing the task ID
     */
    @Before("projectMemberAccess() && args(recordDetailRequest,..)")
    public void beforeCreateOrUpdateRecordDetail(RecordDetailRequest recordDetailRequest) {
        TaskDetail taskDetail = taskDetailRepository.findById(recordDetailRequest.taskId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_DETAIL_NOT_FOUND_MESSAGE, recordDetailRequest.taskId())));
        checkProjectMembership(taskDetail.getProject().getId());
    }

    /**
     * Validates project membership before accessing an entity by its ID.
     * Determines the type of entity being accessed and checks membership accordingly.
     *
     * @param joinPoint the join point providing context about the method being intercepted
     * @param id the ID of the entity being accessed
     */
    @Before("projectMemberAccess() && args(id)")
    public void beforeAccessEntityById(JoinPoint joinPoint, Long id) {
        String className = joinPoint.getSignature().getDeclaringTypeName();

        if (className.contains("TaskDetail")) {
            TaskDetail taskDetail = taskDetailRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_DETAIL_NOT_FOUND_MESSAGE, id)));
            checkProjectMembership(taskDetail.getProject().getId());
        } else if (className.contains("RecordDetail")) {
            RecordDetail recordDetail = recordDetailRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(RECORD_DETAIL_NOT_FOUND_MESSAGE, id)));
            checkProjectMembership(recordDetail.getTask().getProject().getId());
        } else if (className.contains("Project")) {
            checkProjectMembership(id);
        }
    }

    /**
     * Checks if the current authenticated user is a member of the specified project or an admin.
     * Throws AccessDeniedException if the user is neither a member of the project nor an admin.
     *
     * @param projectId the ID of the project to check membership for
     */
    private void checkProjectMembership(Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, currentUserEmail)));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(PROJECT_NOT_FOUND_MESSAGE, projectId)));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!project.getUsers().contains(currentUser) && !isAdmin) {
            throw new AccessDeniedException(ACCESS_DENIED_ERROR_MESSAGE);
        }
    }
}
