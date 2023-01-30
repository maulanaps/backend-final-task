package com.backend.task.services;

import com.backend.task.dto.ReportDto;
import com.backend.task.models.Transaction;
import com.backend.task.models.User;
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

    // report with db relation
    public List<ReportDto> getReports2(LocalDate localDate) {

        Iterable<User> users = userRepo.findAll();
        var report = new ArrayList<ReportDto>();
        List<Transaction> transactions;

        // formatted date
        String balanceChangeDate = localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        // create reportDto per user
        for (var user : users) {

            // get user's transactions on localDate
            transactions = user.getTransactions().stream().filter(transaction ->
                    localDate.equals(transaction.getDate())).collect(Collectors.toList());

            // calculate change in percentage
            String changeInPercentage;
            // if the user didn't do any trx on that date
            if (transactions.size() == 0) {
                changeInPercentage = "0%";
            } // calculation
            else {
                Integer oldBalance = transactions.get(0).getBalanceBefore();
                Integer currentBalance = transactions.get(transactions.size() - 1).getBalanceAfter();

                double numerator = (currentBalance - oldBalance);
                double denominator = oldBalance;

                // if yesterday balance is Rp0,-
                if (denominator == 0) {
                    changeInPercentage = "-";
                } else {
                    DecimalFormat df = new DecimalFormat("###.##");
                    double result = (numerator / denominator) * 100;
                    changeInPercentage = df.format(result) + "%";
                }
            }
            // add it to report list
            report.add(new ReportDto(user.getUsername(), changeInPercentage, balanceChangeDate));
        }
        return report;
    }

    // get data from 2 table (without db relation)
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
    }
}
