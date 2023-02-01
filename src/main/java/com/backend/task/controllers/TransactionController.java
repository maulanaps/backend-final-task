package com.backend.task.controllers;

import com.backend.task.constant.Constants;
import com.backend.task.dto.TransactionTopupDto;
import com.backend.task.dto.TransactionTrfDto;
import com.backend.task.dto.TransactionTrfResponseDto;
import com.backend.task.response.ResponseHandler;
import com.backend.task.services.TransactionServices;
import com.backend.task.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionServices transactionServices;

    @Autowired
    UserServices userServices;
    @PostMapping("/create")
    ResponseEntity<Object> transactionCreate(@Valid @RequestBody TransactionTrfDto transactionTrfDto){

        String username = transactionTrfDto.username();
        String password = transactionTrfDto.password();
        String destinationUsername = transactionTrfDto.destinationUsername();
        Integer amount = transactionTrfDto.amount();

        if (!userServices.existByUsername(username)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user not found");
        }

        if (userServices.isBanned(username)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user is banned");
        }

        if (userServices.isBanned(destinationUsername)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "destination user is banned");
        }

        if (!userServices.validatePassword(username, password)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "password invalid");
        }

        if (!transactionServices.transactionLimitOk(username, amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "transaction limit exceeded");
        }

        if (!transactionServices.minTrfAmountOk(amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "minimum trx amount is "
                    + UserServices.rupiahFormat(Constants.MIN_TRANSACTION));
        }

        if (!transactionServices.balanceIsSufficient(username, amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "not enough balance");
        }

        if (!userServices.existByUsername(destinationUsername)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "destination username not found");
        }

        if (transactionServices.balanceIsOverflow(destinationUsername, amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "destination user balance is overflow");
        }

        TransactionTrfResponseDto transactionTrfResponseDto = transactionServices.executeTransfer(username, destinationUsername, amount);

        return new ResponseEntity<>(transactionTrfResponseDto, HttpStatus.OK);
    }

    @PostMapping("topup")
    ResponseEntity<Object> topup(@RequestBody TransactionTopupDto transactionTopupDto) throws Exception{
        String username = transactionTopupDto.username();
        String password = transactionTopupDto.password();
        Integer amount = transactionTopupDto.amount();

        if (!userServices.existByUsername(username)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user not found");
        }

        if (userServices.isBanned(username)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user is banned");
        }

        if (!userServices.validatePassword(username, password)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "password invalid");
        }

        if (!transactionServices.amountIsValid(amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "amount can't be zero or negative");
        }

        if (transactionServices.balanceIsOverflow(username, amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "max balance exceeded");
        }

        if (!transactionServices.maxTopupOk(amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "max topup exceeded");
        }

        if (!transactionServices.balanceMinimum(username, amount)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "minimum balance is " + Constants.MIN_BALANCE);
        }

        transactionServices.executeTopup(username, amount);

        return ResponseHandler.createResponse(HttpStatus.OK, "OK");
    }
}
