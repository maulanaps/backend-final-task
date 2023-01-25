package com.backend.task.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer trxId;
    Integer userId;
    String type;
    String originUsername;
    String destinationUsername;
    Integer amount;
    Integer balanceBefore;
    Integer balanceAfter;
    String status;
    LocalDate date;

    public Transaction(Integer userId, String type, String originUsername, String destinationUsername
            , Integer amount, Integer balanceBefore, Integer balanceAfter, String status, LocalDate date) {
        this.userId = userId;
        this.type = type;
        this.originUsername = originUsername;
        this.destinationUsername = destinationUsername;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.status = status;
        this.date = date;
    }

    public Transaction() {
    }
}
