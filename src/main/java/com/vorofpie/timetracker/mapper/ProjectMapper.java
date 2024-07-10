package com.vorofpie.timetracker.mapper;

import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.dto.request.ProjectRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    Project toProject(ProjectRequest projectRequest);

    @Mapping(target = "id", ignore = true)
    void updateProjectFromRequest(ProjectRequest projectRequest, @MappingTarget Project project);
}
