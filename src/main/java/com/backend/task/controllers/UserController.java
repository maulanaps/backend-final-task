package com.backend.task.controllers;

import com.backend.task.dto.*;
import com.backend.task.response.ResponseHandler;
import com.backend.task.services.TransactionServices;
import com.backend.task.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServices userServices;

    @PostMapping("/registration")
    ResponseEntity<Object> Registration(@RequestBody UserRegisDto userRegisDto){
        String username = userRegisDto.getUsername();
        String password = userRegisDto.getPassword();

        // validate username
        if (!userServices.validateUsername(username)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "username taken");
        }

        // validate password
        if (!userServices.validateNewCreatedPassword(password)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "password format" +
                    " invalid \n" + "(Min 10 chars, min 1 number, min 1 letter, min 1 special character.)");
        }

        // create new user
        userServices.createUser(userRegisDto);

        return ResponseHandler.createResponse(HttpStatus.OK, "OK");
    }

    @GetMapping("/{username}/getinfo")
    public ResponseEntity<Object> getInfo(@PathVariable String username) {
        if (!userServices.existByUsername(username)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user not found");
        }

        UserInfoDto userInfoDto = userServices.getUserInfo(username);

        return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
    }

    @GetMapping("/{username}/getbalance")
    public ResponseEntity<Object> getBalance(@PathVariable String username) {

        if (!userServices.existByUsername(username)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user not found");
        }

        UserBalanceDto userBalanceDto = userServices.getUserBalance(username);

        return new ResponseEntity<>(userBalanceDto, HttpStatus.OK);
    }

    @PutMapping("/{username}/unban")
    public ResponseEntity<Object> unban(@PathVariable String username) {

        if (!userServices.existByUsername(username)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user not found");
        }

        userServices.unban(username);

        return ResponseHandler.createResponse(HttpStatus.OK, "OK");
    }

    @PutMapping("/{username}/addktp")
    public ResponseEntity<Object> addKtp(@PathVariable String username, @RequestBody UserAddKtp userAddKtp) {

        String ktp = userAddKtp.ktp();

        if (!userServices.existByUsername(username)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user not found");
        }

        if (userServices.existByKtp(ktp)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST
                    , "ktp has been used by other user");
        }

        if (!userServices.validateKtp(ktp)) {
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "incorrect ktp format");
        }

        userServices.updateKtp(username, ktp);

        return ResponseHandler.createResponse(HttpStatus.OK, "OK");
    }

    @PostMapping("/changepassword")
    public ResponseEntity<Object> changePassword(@RequestBody UserChangePassDto userChangePassDto) {

        String username = userChangePassDto.username();
        String password = userChangePassDto.password();
        String oldPassword = userChangePassDto.oldPassword();


        if (!userServices.existByUsername(username)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user not found");
        }

        if (userServices.isBanned(username)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "user is banned");
        }

        if (!userServices.validatePassword(username, oldPassword)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "old password doesn't match");
        }

        if (!userServices.validateNewCreatedPassword(password)){
            return ResponseHandler.createResponse(HttpStatus.BAD_REQUEST, "format invalid \n" +
                    "(Min 10 chars, min 1 number, min 1 letter, min 1 special character.)");
        }

        userServices.changePassword(username, password);

        return ResponseHandler.createResponse(HttpStatus.OK, "OK");
    }
}