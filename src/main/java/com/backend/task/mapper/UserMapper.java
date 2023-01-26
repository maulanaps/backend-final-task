package com.backend.task.mapper;

import com.backend.task.dto.UserBalanceDto;
import com.backend.task.dto.UserInfoDto;
import com.backend.task.dto.UserRegisDto;
import com.backend.task.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserInfoDto toUserInfoDto(User user);

    @Mapping(target = "balance", source = "balance", numberFormat = "Rp###,###")
    @Mapping(target = "transactionLimit", source = "transactionLimit", numberFormat = "Rp###,###")
    UserBalanceDto toUserBalanceDto(User user);

    User toUser(UserRegisDto userRegisDto);
}