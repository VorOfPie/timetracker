package com.vorofpie.timetracker.mapper;

import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.dto.request.TaskDetailRequest;
import com.vorofpie.timetracker.dto.response.TaskDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskDetailMapper {

    TaskDetailResponse toTaskDetailResponse(TaskDetail taskDetail);

    TaskDetail toTaskDetail(TaskDetailRequest taskDetailRequest);

    @Mapping(target = "id", ignore = true)
    void updateTaskDetailFromRequest(TaskDetailRequest taskDetailRequest, @MappingTarget TaskDetail taskDetail);
}
