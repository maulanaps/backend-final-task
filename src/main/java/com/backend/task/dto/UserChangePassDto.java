package com.backend.task.dto;

import jakarta.validation.constraints.NotBlank;

public record UserChangePassDto(
        @NotBlank(message = "username is required")
        String username,
        @NotBlank(message = "old password is required")
        String oldPassword,
        @NotBlank(message = "password (new) is required")
        String password
)
{}
