package com.vorofpie.timetracker.controller;

import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.dto.response.TaskDetailResponse;
import com.vorofpie.timetracker.service.TaskDetailService;
import com.vorofpie.timetracker.swagger.TaskDetailApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskDetailController implements TaskDetailApi {

    private final TaskDetailService taskDetailService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public TaskDetailResponse createTaskDetail(@RequestBody @Valid TaskDetailRequest taskDetailRequest) {
        return taskDetailService.createTaskDetail(taskDetailRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public TaskDetailResponse updateTaskDetail(@PathVariable Long id, @RequestBody @Valid TaskDetailRequest taskDetailRequest) {
        return taskDetailService.updateTaskDetail(id, taskDetailRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void deleteTaskDetail(@PathVariable Long id) {
        taskDetailService.deleteTaskDetail(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public TaskDetailResponse getTaskDetailById(@PathVariable Long id) {
        return taskDetailService.getTaskDetailById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<TaskDetailResponse> getAllTaskDetails() {
        return taskDetailService.getAllTaskDetails();
    }
}
