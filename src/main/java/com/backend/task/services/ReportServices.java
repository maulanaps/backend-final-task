package com.backend.task.services;

import com.backend.task.dto.ReportDto;
import com.backend.task.models.Transaction;
import com.backend.task.repo.TransactionRepo;
import com.backend.task.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServices {
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    UserRepo userRepo;

    public List<ReportDto> getReports(LocalDate localDate) {

        // get all user's username
        List<String> usersUsername = userRepo.findAllUsername();

        // get all trx on certain dat
        List<Transaction> transactions = transactionRepo.findAllByDate(localDate);

        // formatted date
        String balanceChangeDate = localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        // Map users transactions into hashmap
        var map = new HashMap<String, List<Transaction>>();
        usersUsername.forEach((username -> {
            List<Transaction> userTrx = transactions.stream().filter(transaction ->
                    username.equals(transaction.getUsername())).collect(Collectors.toList());
            map.put(username, userTrx);
        }));

        // calculate change in balance per user
        var report = new ArrayList<ReportDto>();
        map.forEach((username, transactionList) -> {

            String changeInPercentage = "";

            // if the user didn't do any trx on that date
            if (transactionList.size() == 0) {
                changeInPercentage = "0%";
            } // calculation
            else {
                Integer oldestBalance = transactionList.get(0).getBalanceBefore();
                Integer currentBalance = transactionList.get(transactionList.size() - 1).getBalanceAfter();

                int numerator = currentBalance - oldestBalance;
                double denominator = oldestBalance * 1.0;

                // if yesterday balance is Rp0,-
                if (denominator == 0) {
                    changeInPercentage = "-";
                } else {
                    DecimalFormat df = new DecimalFormat("###.##");
                    double result = (numerator / denominator) * 100;
                    changeInPercentage = df.format(result) + "%";
                }
            }

            // make ReportDto object per user
            // add it to report list
            report.add(new ReportDto(username, changeInPercentage, balanceChangeDate));
        });

        return report;

//        // initialize response dto
//        List<ReportDto> reports = new ArrayList<>();
//
//        // get transactions by date
//        List<Transaction> transactions = transactionRepo.findAllByDate(localDate);
//
//        // get all user id
//        Set<Integer> userIds = new HashSet<>();
//        for (var transaction : transactions) {
//            userIds.add(transaction.getUser().getId());
//        }
//
//        // get all transaction of each user
//        Integer latestBalance;
//        Integer oldestBalance;
//        List<Transaction> userTransaction = new ArrayList<>();
//        for (var userId : userIds){
//
//            // clear user transaction list
//            userTransaction.clear();
//
//            // add transaction of the user
//            for (var transaction : transactions) {
//                if (transaction.getUser().getId() == userId){
//                    userTransaction.add(transaction);
//                }
//            }
//
//            // sort by trxId (oldest to latest trx)
//            Collections.sort(userTransaction, new SortbyTrxId());
//
//            // response data
//            oldestBalance = userTransaction.get(0).getBalanceBefore();
//            latestBalance = userTransaction.get(userTransaction.size() - 1).getBalanceAfter();
//
//            User user = userRepo.getById(userId);
//            String username = user.getUsername();
//
//            // calculation
//            Integer numerator = latestBalance - oldestBalance;
//            Float denominator = oldestBalance.floatValue();
//            Float temp = numerator / denominator * 100;
//            String changeInPercentage;
//            String balanceChangeDate = localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
//
//            if (denominator <= 0){
//                changeInPercentage = "-";
//            } else {
//                changeInPercentage = temp + "%";
//            }
//
//            // add into reportDto list
//            reports.add(new ReportDto(username, changeInPercentage, balanceChangeDate));
//        }
//
//        return reports;
    }

    class SortbyTrxId implements Comparator<Transaction> {
        // Used for sorting in ascending order of
        @Override
        public int compare(Transaction a, Transaction b) {
            return a.getTrxId() + b.getTrxId();
        }
    }
}
