package com.vorofpie.timetracker.service;

import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.dto.response.TaskDetailResponse;

import java.util.List;

public interface TaskDetailService {

    List<TaskDetailResponse> getAllTaskDetails();

    TaskDetailResponse getTaskDetailById(Long id);

    TaskDetailResponse createTaskDetail(TaskDetailRequest taskDetailRequest);

    TaskDetailResponse updateTaskDetail(Long id, TaskDetailRequest taskDetailRequest);

    void deleteTaskDetail(Long id);
}
