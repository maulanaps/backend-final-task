package com.backend.task.dto;

import com.backend.task.constant.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisDto {
    String username;
    String password;
    Integer balance = 0;
    Integer transactionLimit = Constants.MAX_TRANSACTION_NO_KTP;
}