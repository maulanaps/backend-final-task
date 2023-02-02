package com.backend.task.repo;

import com.backend.task.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Integer> {
    User findByUsername(String username);
    User findByKtp(String ktp);
}
