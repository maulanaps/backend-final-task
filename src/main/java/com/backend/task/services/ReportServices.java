package com.backend.task.services;

import com.backend.task.dto.ReportDto;
import com.backend.task.models.Transaction;
import com.backend.task.models.User;
import com.backend.task.repo.TransactionRepo;
import com.backend.task.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportServices {
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    UserRepo userRepo;

    public List<ReportDto> getReports(LocalDate localDate){

        // initialize response dto
        List<ReportDto> reports = new ArrayList<>();

        // get transactions by date
        List<Transaction> transactions = transactionRepo.findAllByDate(localDate);

        // get all user id
        Set<Integer> userIds = new HashSet<>();
        for (var transaction : transactions) {
            userIds.add(transaction.getUser().getId());
        }

        // get all transaction of each user
        Integer latestBalance;
        Integer oldestBalance;
        List<Transaction> userTransaction = new ArrayList<>();
        for (var userId : userIds){

            // clear user transaction list
            userTransaction.clear();

            // add transaction of the user
            for (var transaction : transactions) {
                if (transaction.getUser().getId() == userId){
                    userTransaction.add(transaction);
                }
            }

            // sort by trxId (oldest to latest trx)
            Collections.sort(userTransaction, new SortbyTrxId());

            // response data
            oldestBalance = userTransaction.get(0).getBalanceBefore();
            latestBalance = userTransaction.get(userTransaction.size() - 1).getBalanceAfter();

            User user = userRepo.getById(userId);
            String username = user.getUsername();

            // calculation
            Integer numerator = latestBalance - oldestBalance;
            Float denominator = oldestBalance.floatValue();
            Float temp = numerator / denominator * 100;
            String changeInPercentage;
            String balanceChangeDate = localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

            if (denominator <= 0){
                changeInPercentage = "-";
            } else {
                changeInPercentage = temp + "%";
            }

            // add into reportDto list
            reports.add(new ReportDto(username, changeInPercentage, balanceChangeDate));
        }

        return reports;
    }

    class SortbyTrxId implements Comparator<Transaction>
    {
        // Used for sorting in ascending order of
        @Override
        public int compare(Transaction a, Transaction b) {
            return a.getTrxId() + b.getTrxId();
        }
    }
}
