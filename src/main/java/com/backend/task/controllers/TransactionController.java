package com.backend.task.controllers;

import com.backend.task.constant.Constants;
import com.backend.task.dto.TransactionTopupDto;
import com.backend.task.dto.TransactionTrfDto;
import com.backend.task.dto.TransactionTrfResponseDto;
import com.backend.task.services.TransactionServices;
import com.backend.task.services.UserServices;
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
    ResponseEntity<Object> transactionCreate(@RequestBody TransactionTrfDto transactionTrfDto){

        String username = transactionTrfDto.username();
        String password = transactionTrfDto.password();
        String destinationUsername = transactionTrfDto.destinationUsername();
        Integer amount = transactionTrfDto.amount();

        if (!userServices.existByUsername(username)){
            return new ResponseEntity<>("400 - user not found", HttpStatus.BAD_REQUEST);
        }

        if (userServices.isBanned(username)){
            return new ResponseEntity<>("400 - user is banned", HttpStatus.BAD_REQUEST);
        }

        if (userServices.isBanned(destinationUsername)){
            return new ResponseEntity<>("400 - destination user is banned", HttpStatus.BAD_REQUEST);
        }

        if (!userServices.validatePassword(username, password)){
            return new ResponseEntity<>("400 - password invalid", HttpStatus.BAD_REQUEST);
        }

        if (!transactionServices.transactionLimitOk(username, amount)){
            return new ResponseEntity<>("400 - transaction limit exceeded", HttpStatus.BAD_REQUEST);
        }

        if (!transactionServices.minTrfAmountOk(amount)){
            return new ResponseEntity<>("400 - minimum trx amount is "
                    + UserServices.rupiahFormat(Constants.MIN_TRANSACTION),HttpStatus.BAD_REQUEST);
        }

        if (!transactionServices.balanceIsSufficient(username, amount)){
            return new ResponseEntity<>("400 - not enough balance", HttpStatus.BAD_REQUEST);
        }

        if (!userServices.existByUsername(destinationUsername)){
            return new ResponseEntity<>("400 - destination username not found", HttpStatus.BAD_REQUEST);
        }

        if (transactionServices.balanceIsOverflow(destinationUsername, amount)){
            return new ResponseEntity<>("400 - destination user balance is overflow", HttpStatus.BAD_REQUEST);
        }

        TransactionTrfResponseDto transactionTrfResponseDto = transactionServices.executeTransfer(username, destinationUsername, amount);

        return new ResponseEntity<>(transactionTrfResponseDto, HttpStatus.OK);
//
//        400 - user not found
//        400 - password invalid
//        400 - user banned
//        400 - not enough balance
//        400 - transaction limit exceeded
//        400 - minimum trx amount is xxxxxxxx
//        execute trf

//        400 - format invalid
    }

    @PostMapping("topup")
    ResponseEntity<Object> topup(@RequestBody TransactionTopupDto transactionTopupDto){
        String username = transactionTopupDto.username();
        String password = transactionTopupDto.password();
        Integer amount = transactionTopupDto.amount();

        if (!userServices.existByUsername(username)){
            return new ResponseEntity<>("400 - user not found", HttpStatus.BAD_REQUEST);
        }

        if (userServices.isBanned(username)){
            return new ResponseEntity<>("400 - user is banned", HttpStatus.BAD_REQUEST);
        }

        if (!userServices.validatePassword(username, password)){
            return new ResponseEntity<>("400 - password invalid", HttpStatus.BAD_REQUEST);
        }

        if (transactionServices.balanceIsOverflow(username, amount)){
            return new ResponseEntity<>("400 - max balance exceeded", HttpStatus.BAD_REQUEST);
        }

        if (!transactionServices.maxTopupOk(amount)){
            return new ResponseEntity<>("400 - max topup exceeded", HttpStatus.BAD_REQUEST);
        }

        transactionServices.executeTopup(username, amount);

        return new ResponseEntity<>("200 - OK", HttpStatus.OK);
    }
}
