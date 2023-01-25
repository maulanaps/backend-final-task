package com.backend.task.services;

import com.backend.task.constant.Constants;
import com.backend.task.dto.TransactionTrfResponseDto;
import com.backend.task.models.Transaction;
import com.backend.task.models.User;
import com.backend.task.repo.TransactionRepo;
import com.backend.task.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionServices {

    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    UserRepo userRepo;

    public Boolean transactionLimitOk(String username, Integer trfAmount){
        User user = userRepo.findByUsername(username);

        return trfAmount <= user.getTransactionLimit();
    }

    public Boolean minTrfAmountOk(Integer amount){
        return amount >= Constants.MIN_TRANSACTION;
    }

    public Boolean balanceIsSufficient(String username, Integer amount){
        User user = userRepo.findByUsername(username);

        Integer amountAfterTax = Math.round(amount * (1 + Constants.TAX));

        int balanceAfter = user.getBalance() - amountAfterTax;

        return balanceAfter >= Constants.MIN_BALANCE;
    }

    public Boolean balanceIsOverflow(String username, Integer amount){
        User user = userRepo.findByUsername(username);
        int balanceAfter = user.getBalance() + amount;

        return balanceAfter > Constants.MAX_BALANCE;
    }

    public Boolean maxTopupOk(Integer amount){
        return amount <= Constants.MAX_TOPUP;
    }

    public TransactionTrfResponseDto executeTransfer(String originUsername, String destinationUsername, Integer amount){

        Integer amountAfterTax = Math.round(amount * (1 + Constants.TAX));

        // origin user
        User originUser = userRepo.findByUsername(originUsername);
        Integer originUserBalanceBefore = originUser.getBalance();
        Integer originUserBalance = originUserBalanceBefore - amountAfterTax;

        // destination user
        User destinationUser = userRepo.findByUsername(destinationUsername);
        Integer destinationUserBalanceBefore = destinationUser.getBalance();;
        Integer destinationUserBalance = destinationUserBalanceBefore + amount;

        // update balance of 2 user
        destinationUser.setBalance(destinationUserBalance);
        originUser.setBalance(originUserBalance);

        LocalDate localDate = LocalDate.now();

        // save 2 users updated balances
        userRepo.save(originUser);
        userRepo.save(destinationUser);


        // update transaction db (origin user)
        Transaction transactionOrigin = new Transaction(originUser.getId(), "SEND", originUsername
                , destinationUsername, amount, originUserBalanceBefore, originUserBalance, "SETTLED", localDate);
        transactionRepo.save(transactionOrigin);

        // update transaction db (destination user)
        Transaction transactionDestination = new Transaction(destinationUser.getId(), "RECEIVE", originUsername
                , destinationUsername, amount, destinationUserBalanceBefore, destinationUserBalance, "SETTLED", localDate);
        transactionRepo.save(transactionDestination);

        return new TransactionTrfResponseDto(transactionOrigin.getTrxId(), originUsername, destinationUsername, amount, "SETTLED");
    }

    public void executeTopup(String username, Integer amount){
        // get user
        User user = userRepo.findByUsername(username);
        Integer userBalanceBefore = user.getBalance();
        Integer userBalanceAfter = userBalanceBefore + amount;

        // update and save balance
        user.setBalance(userBalanceAfter);
        userRepo.save(user);

        // get current date
        LocalDate localDate = LocalDate.now();

        // update transaction db
        Transaction transaction = new Transaction(user.getId(), "TOPUP", null
                , username, amount, userBalanceBefore, userBalanceAfter, "SETTLED", localDate);
        transactionRepo.save(transaction);
    }
}
