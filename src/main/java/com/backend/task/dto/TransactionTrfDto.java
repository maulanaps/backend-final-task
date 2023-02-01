package com.backend.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

//@Data
public record TransactionTrfDto(
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "Password is required")
        String password,
        @NotBlank(message = "Destination username is required")
        String destinationUsername,
        @NotNull(message = "Amount is required")
        Integer amount
){}