package com.backend.task.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private Integer balance;
    private Integer transactionLimit;
    private String ktp;
    private Boolean ban = false;
    private int incorrectPasswordCount = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }
}