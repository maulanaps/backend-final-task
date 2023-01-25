package com.backend.task.dto;

public record UserChangePassDto(String username, String oldPassword, String password) {
}
