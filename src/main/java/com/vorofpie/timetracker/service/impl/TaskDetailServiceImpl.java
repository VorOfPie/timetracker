package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.dto.response.TaskDetailResponse;
import com.vorofpie.timetracker.mapper.TaskDetailMapper;
import com.vorofpie.timetracker.repository.TaskDetailRepository;
import com.vorofpie.timetracker.service.TaskDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskDetailServiceImpl implements TaskDetailService {

    private final TaskDetailRepository taskDetailRepository;
    private final TaskDetailMapper taskDetailMapper;

    @Override
    public List<TaskDetailResponse> getAllTaskDetails() {
        return taskDetailRepository.findAll().stream()
                .map(taskDetailMapper::toTaskDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDetailResponse getTaskDetailById(Long id) {
        TaskDetail taskDetail = taskDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task detail not found"));
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    @Override
    @Transactional
    public TaskDetailResponse createTaskDetail(TaskDetailRequest taskDetailRequest) {
        TaskDetail taskDetail = taskDetailMapper.toTaskDetail(taskDetailRequest);
        taskDetail = taskDetailRepository.save(taskDetail);
        return taskDetailMapper.toTaskDetailResponse(taskDetail);
    }

    @Override
    @Transactional
    public TaskDetailResponse updateTaskDetail(Long id, TaskDetailRequest taskDetailRequest) {
        TaskDetail existingTaskDetail = taskDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task detail not found"));
        taskDetailMapper.updateTaskDetailFromRequest(taskDetailRequest, existingTaskDetail);
        existingTaskDetail = taskDetailRepository.save(existingTaskDetail);
        return taskDetailMapper.toTaskDetailResponse(existingTaskDetail);
    }

    @Override
    public void deleteTaskDetail(Long id) {
        taskDetailRepository.deleteById(id);
    }
}
