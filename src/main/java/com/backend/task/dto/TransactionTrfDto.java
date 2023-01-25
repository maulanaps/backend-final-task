package com.backend.task.dto;

public record TransactionTrfDto(String username, String password, String destinationUsername, Integer amount) {
}
