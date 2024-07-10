package com.vorofpie.timetracker.mapper;

import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toUserResponse(User user);

    User toUser(UserRequest userRequest);

    @Mapping(target = "id", ignore = true)
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget User user);
}
