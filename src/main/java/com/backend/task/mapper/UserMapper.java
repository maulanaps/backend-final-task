package com.backend.task.mapper;

import com.backend.task.dto.UserInfoDto;
import com.backend.task.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserInfoDto toUserInfoDto(User user);
}