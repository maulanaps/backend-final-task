package com.backend.task.dto;

public record TransactionTrfResponseDto(Integer trxId, String originUsername, String destinationUsername,
                                        Integer amount, String status) {

}
