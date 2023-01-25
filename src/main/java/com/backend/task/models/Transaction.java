package com.backend.task.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Setter(AccessLevel.NONE)
    Integer trxId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    String type;
    String originUsername;
    String destinationUsername;
    Integer amount;
    Integer balanceBefore;
    Integer balanceAfter;
    String status;
    LocalDate date;

    public Transaction(String type, String originUsername, String destinationUsername
            , Integer amount, Integer balanceBefore, Integer balanceAfter, String status, LocalDate date, User user) {
        this.type = type;
        this.originUsername = originUsername;
        this.destinationUsername = destinationUsername;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.status = status;
        this.date = date;
        this.user = user;
    }

    public Transaction() {
    }
}
