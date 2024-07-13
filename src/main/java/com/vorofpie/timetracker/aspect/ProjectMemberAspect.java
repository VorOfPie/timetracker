package com.vorofpie.timetracker.aspect;

import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.domain.RecordDetail;
import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
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

@Aspect
@Component
@RequiredArgsConstructor
public class ProjectMemberAspect {

    private final UserRepository userRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final ProjectRepository projectRepository;
    private final RecordDetailRepository recordDetailRepository;

    @Pointcut("@annotation(com.vorofpie.timetracker.aspect.annotation.ProjectMemberAccess)")
    public void projectMemberAccess() {
    }

    @Before("projectMemberAccess() && args(taskDetailRequest,..)")
    public void beforeCreateOrUpdateTaskDetail(TaskDetailRequest taskDetailRequest) {
        checkProjectMembership(taskDetailRequest.projectId());
    }

    @Before("projectMemberAccess() && args(recordDetailRequest,..)")
    public void beforeCreateOrUpdateRecordDetail(RecordDetailRequest recordDetailRequest) {
        TaskDetail taskDetail = taskDetailRepository.findById(recordDetailRequest.taskId())
                .orElseThrow(() -> new RuntimeException("Task detail not found"));
        checkProjectMembership(taskDetail.getProject().getId());
    }

    @Before("projectMemberAccess() && args(id)")
    public void beforeAccessEntityById(JoinPoint joinPoint, Long id) {
        String className = joinPoint.getSignature().getDeclaringTypeName();

        if (className.contains("TaskDetail")) {
            TaskDetail taskDetail = taskDetailRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task detail not found"));
            checkProjectMembership(taskDetail.getProject().getId());
        } else if (className.contains("RecordDetail")) {
            RecordDetail recordDetail = recordDetailRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Record detail not found"));
            checkProjectMembership(recordDetail.getTask().getProject().getId());
        } else if (className.contains("Project")) {
            checkProjectMembership(id);
        }
    }

    private void checkProjectMembership(Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUsers().contains(currentUser) && !authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Access denied: User is not a member of the project");
        }
    }
}
