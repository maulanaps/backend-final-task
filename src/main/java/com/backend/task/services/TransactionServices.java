package com.backend.task.services;

import com.backend.task.audit.AbstractAuditingEntity;
import com.backend.task.constant.Constants;
import com.backend.task.dto.TransactionTrfResponseDto;
import com.backend.task.models.Transaction;
import com.backend.task.models.User;
import com.backend.task.repo.TransactionRepo;
import com.backend.task.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServices extends AbstractAuditingEntity {

    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    UserRepo userRepo;

    public Boolean transactionLimitOk(String username, Integer trfAmount) {
        User user = userRepo.findByUsername(username);

        return trfAmount <= user.getTransactionLimit();
    }

    public Boolean minTrfAmountOk(Integer amount) {
        return amount >= Constants.MIN_TRANSACTION;
    }

    public Boolean balanceIsSufficient(String username, Integer amount) {
        User user = userRepo.findByUsername(username);

        Integer amountAfterTax = Math.round(amount * (1 + Constants.TAX));

        int balanceAfter = user.getBalance() - amountAfterTax;

        return balanceAfter >= Constants.MIN_BALANCE;
    }

    public Boolean balanceIsOverflow(String username, Integer amount) {
        User user = userRepo.findByUsername(username);
        int balanceAfter = user.getBalance() + amount;

        return balanceAfter > Constants.MAX_BALANCE;
    }

    public Boolean maxTopupOk(Integer amount) {
        return amount <= Constants.MAX_TOPUP;
    }

    public TransactionTrfResponseDto executeTransfer(String originUsername, String destinationUsername, Integer amount) {

        int tax = Math.round(amount * Constants.TAX);

        // origin user
        User originUser = userRepo.findByUsername(originUsername);
        int originUserBalanceBefore = originUser.getBalance();
        int originUserBalanceMid = originUserBalanceBefore - amount;
        int originUserBalanceAfter = originUserBalanceMid - tax;

        // destination user
        User destinationUser = userRepo.findByUsername(destinationUsername);
        int destinationUserBalanceBefore = destinationUser.getBalance();
        int destinationUserBalanceAfter = destinationUserBalanceBefore + amount;

        // update balance of 2 user
        destinationUser.setBalance(destinationUserBalanceAfter);
        originUser.setBalance(originUserBalanceAfter);

        LocalDate localDate = LocalDate.now();

        // create transaction (origin user)
        Transaction transactionOrigin = new Transaction("SEND", originUsername, amount * (-1)
                , originUserBalanceBefore, originUserBalanceMid
                , "SETTLED", localDate, originUser);

        // create TAX transaction (origin user)
        Transaction transactionOriginTax = new Transaction("TAX", originUsername, tax * (-1)
                , originUserBalanceMid, originUserBalanceAfter
                , "SETTLED", localDate, originUser);

        // create transaction (destination user)
        Transaction transactionDestination = new Transaction("RECEIVE", destinationUsername
                , amount, destinationUserBalanceBefore, destinationUserBalanceAfter
                , "SETTLED", localDate, destinationUser);

        // save transactions
        transactionRepo.saveAll(List.of(transactionOrigin, transactionOriginTax, transactionDestination));

        return new TransactionTrfResponseDto(transactionOrigin.getTrxId(), originUsername
                , destinationUsername, amount, "SETTLED");
    }

    public void executeTopup(String username, Integer amount) {
        // get user
        User user = userRepo.findByUsername(username);
        Integer userBalanceBefore = user.getBalance();
        Integer userBalanceAfter = userBalanceBefore + amount;

        // update user balance
        user.setBalance(userBalanceAfter);

        // get current date
        LocalDate localDate = LocalDate.now();

        // create transaction
        Transaction transaction = new Transaction("TOPUP", username, amount, userBalanceBefore
                , userBalanceAfter, "SETTLED", localDate, user);

        // save transaction
        transactionRepo.save(transaction);
    }

    public boolean balanceMinimum(String username, Integer amount) {
        User user = userRepo.findByUsername(username);

        int balanceAfter = user.getBalance() + amount;

        return balanceAfter >= Constants.MIN_BALANCE;
    }

    public boolean amountIsValid(Integer amount){
        if (amount == 0 || Math.signum(amount) == -1){
            return false;
        }
        return true;
    }
}
