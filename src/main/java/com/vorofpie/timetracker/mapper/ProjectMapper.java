package com.vorofpie.timetracker.mapper;

import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.dto.request.CreateProjectRequest;
import com.vorofpie.timetracker.dto.request.UpdateProjectRequest;
import com.vorofpie.timetracker.dto.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    Project toProject(CreateProjectRequest createProjectRequest);

    Project updateProjectFromProjectRequest(UpdateProjectRequest updateProjectRequest,@MappingTarget Project project);

}
