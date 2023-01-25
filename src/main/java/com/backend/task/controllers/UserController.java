package com.backend.task.controllers;

import com.backend.task.dto.*;
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
    @Autowired
    TransactionServices transactionServices;

    @PostMapping("/registration")
    ResponseEntity<String> Registration(@RequestBody UserRegisDto userRegisDto){
        String username = userRegisDto.username();
        String password = userRegisDto.password();

        // validate username
        if (!userServices.validateUsername(username)) {
            return new ResponseEntity<>("400 - username taken", HttpStatus.BAD_REQUEST);
        }

        // validate password
        if (!userServices.validateNewCreatedPassword(password)) {
            return new ResponseEntity<>("400 - password format invalid \n"
                    + "(Min 10 chars, min 1 number, min 1 letter, min 1 special character.)"
                    , HttpStatus.BAD_REQUEST);
        }

        // create new user
        userServices.createUser(userRegisDto);

        return new ResponseEntity<>("200 - OK", HttpStatus.OK);
    }

    @GetMapping("/{username}/getinfo")
    public ResponseEntity<Object> getInfo(@PathVariable String username) {
        if (!userServices.existByUsername(username)) {
            return new ResponseEntity<>("400 - user not found", HttpStatus.BAD_REQUEST);
        }

        UserInfoDto userInfoDto = userServices.getUserInfo(username);

        return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
    }

    @GetMapping("/{username}/getbalance")
    public ResponseEntity<Object> getBalance(@PathVariable String username) {

        if (!userServices.existByUsername(username)) {
            return new ResponseEntity<>("400 - user not found", HttpStatus.BAD_REQUEST);
        }

        UserBalanceDto userBalanceDto = userServices.getUserBalance(username);

        return new ResponseEntity<>(userBalanceDto, HttpStatus.OK);
    }

    @PutMapping("/{username}/unban")
    public ResponseEntity<String> unban(@PathVariable String username) {

        if (!userServices.existByUsername(username)) {
            return new ResponseEntity<>("400 - user not found", HttpStatus.BAD_REQUEST);
        }

        userServices.unban(username);

        return new ResponseEntity<>("200 - OK", HttpStatus.OK);
    }

    @PutMapping("/{username}/addktp")
    public ResponseEntity<String> addKtp(@PathVariable String username, @RequestBody UserAddKtp userAddKtp) {

        String ktp = userAddKtp.ktp();

        if (!userServices.existByUsername(username)) {
            return new ResponseEntity<>("400 - user not found", HttpStatus.BAD_REQUEST);
        }

        if (userServices.existByKtp(ktp)) {
            return new ResponseEntity<>("400 - ktp has been used by other user", HttpStatus.BAD_REQUEST);
        }

        if (!userServices.validateKtp(ktp)) {
            return new ResponseEntity<>("400 - incorrect ktp format", HttpStatus.BAD_REQUEST);
        }

        userServices.updateKtp(username, ktp);

        return new ResponseEntity<>("200 - OK", HttpStatus.OK);
    }

    @PostMapping("/changepassword")
    public ResponseEntity<String> changePassword(@RequestBody UserChangePassDto userChangePassDto) {

        String username = userChangePassDto.username();
        String password = userChangePassDto.password();
        String oldPassword = userChangePassDto.oldPassword();


        if (!userServices.existByUsername(username)){
            return new ResponseEntity<>("400 - user not found", HttpStatus.BAD_REQUEST);
        }

        if (userServices.isBanned(username)){
            return new ResponseEntity<>("400 - user is banned", HttpStatus.BAD_REQUEST);
        }

        if (!userServices.validatePassword(username, oldPassword)){
            return new ResponseEntity<>("400 - old password doesn't match", HttpStatus.BAD_REQUEST);
        }

        if (!userServices.validateNewCreatedPassword(password)){
            return new ResponseEntity<>("400 - format invalid \n" +
                    "(Min 10 chars, min 1 number, min 1 letter, min 1 special character.)"
                    , HttpStatus.BAD_REQUEST);
        }

        userServices.changePassword(username, password);

        return new ResponseEntity<>("200 - OK", HttpStatus.OK);
    }
}