package com.backend.task.repo;

import com.backend.task.models.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends CrudRepository<Transaction, Integer> {
    List <Transaction> findAllByDate(LocalDate localDate);
}
