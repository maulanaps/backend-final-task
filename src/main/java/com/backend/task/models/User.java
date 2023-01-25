package com.backend.task.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;
    private String username;
    private String password;
    private Integer balance;
    private Integer transactionLimit;
    private String ktp;
    private Boolean ban = false;
    private int incorrectPasswordCount = 0;
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }
}