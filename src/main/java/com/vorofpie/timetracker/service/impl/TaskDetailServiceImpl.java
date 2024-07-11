package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.dto.response.TaskDetailResponse;
import com.vorofpie.timetracker.mapper.TaskDetailMapper;
import com.vorofpie.timetracker.repository.ProjectRepository;
import com.vorofpie.timetracker.repository.TaskDetailRepository;
import com.vorofpie.timetracker.service.TaskDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskDetailServiceImpl implements TaskDetailService {

    private final TaskDetailRepository taskDetailRepository;
    private final TaskDetailMapper taskDetailMapper;
    private final ProjectRepository projectRepository;

    @Override
    public List<TaskDetailResponse> getAllTaskDetails() {
        return taskDetailRepository.findAll().stream()
                .map(taskDetailMapper::toTaskDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDetailResponse getTaskDetailById(Long id) {
        TaskDetail taskDetail = findTaskDetailByIdOrThrow(id);
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    @Override
    public TaskDetailResponse createTaskDetail(TaskDetailRequest taskDetailRequest) {
        Project project = findProjectByIdOrThrow(taskDetailRequest.projectId());
        TaskDetail taskDetail = taskDetailMapper.toTaskDetail(taskDetailRequest);
        taskDetail.setProject(project);
        taskDetail = taskDetailRepository.save(taskDetail);
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    @Override
    public TaskDetailResponse updateTaskDetail(Long id, TaskDetailRequest taskDetailRequest) {
        TaskDetail existingTaskDetail = findTaskDetailByIdOrThrow(id);
        Project project = findProjectByIdOrThrow(taskDetailRequest.projectId());
        taskDetailMapper.updateTaskDetailFromRequest(taskDetailRequest, existingTaskDetail);
        existingTaskDetail.setProject(project);
        existingTaskDetail = taskDetailRepository.save(existingTaskDetail);
        return taskDetailMapper.toTaskDetailResponse(existingTaskDetail);
    }


    @Override
    public void deleteTaskDetail(Long id) {
        taskDetailRepository.deleteById(id);
    }

    private TaskDetail findTaskDetailByIdOrThrow(Long id) {
        return taskDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task detail not found"));
    }
    private Project findProjectByIdOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id " + projectId));
    }


}
