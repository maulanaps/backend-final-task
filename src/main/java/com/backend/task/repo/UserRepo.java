package com.backend.task.repo;

import com.backend.task.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
    User findByUsername(String username);
    User findByKtp(String ktp);
    User getById(Integer id);
}
