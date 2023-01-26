package com.backend.task.services;

import com.backend.task.constant.Constants;
import com.backend.task.dto.UserBalanceDto;
import com.backend.task.dto.UserInfoDto;
import com.backend.task.dto.UserRegisDto;
import com.backend.task.mapper.UserMapper;
import com.backend.task.models.User;
import com.backend.task.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserServices {
    @Autowired
    UserRepo userRepo;

    @Autowired
    UserMapper userMapper;

    public void createUser(UserRegisDto userRegisDto) {
        User user = new User(userRegisDto.username(), userRegisDto.password());
        user.setBalance(Constants.MIN_BALANCE);
        user.setTransactionLimit(Constants.MAX_TRANSACTION_NO_KTP);
        userRepo.save(user);
    }

    public Boolean existByUsername(String username) {

        return userRepo.findByUsername(username) != null;
    }

    public Boolean validateUsername(String username) {
        // true if username didn't exist
        return userRepo.findByUsername(username) == null;
    }

    public Boolean validateNewCreatedPassword(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?_&(){},./<>?;':\"])[A-Za-z\\d@$!%*#?_&(){},./<>?;':\"]{10,}$";

        return password.matches(regex);
    }

    public Boolean existByKtp(String ktp) {
        // true if ktp exist
        return userRepo.findByKtp(ktp) != null;
    }

    public Boolean validateKtp(String ktp){
        String regex = String.format("^[0-9]{%d}$", Constants.KTP_LENGTH);

        return ktp.matches(regex);
    }

    public UserInfoDto getUserInfo(String username) {
        User userFound = userRepo.findByUsername(username);

        if (userFound == null) {
            // user not found
            return null;
        }

//        return new UserInfoDto(userFound.getUsername(), userFound.getKtp());
        return userMapper.toUserInfoDto(userFound);
    }

    public UserBalanceDto getUserBalance(String username) {
        User userFound = userRepo.findByUsername(username);

        if (userFound == null) {
            // user not found
            return null;
        }

        String balanceFormatted = rupiahFormat(userFound.getBalance());
        String transactionLimitFormatted = rupiahFormat(userFound.getTransactionLimit());

        return new UserBalanceDto(balanceFormatted, transactionLimitFormatted);
    }

    public void updateKtp(String username, String ktp){
        User user = userRepo.findByUsername(username);
        user.setKtp(ktp);
        user.setTransactionLimit(Constants.MAX_TRANSACTION_W_KTP);

        userRepo.save(user);
    }

    public void unban(String username){
        User user = userRepo.findByUsername(username);
        user.setBan(false);
        userRepo.save(user);
    }

    public Boolean validatePassword(String username, String passwordInput){
        User user = userRepo.findByUsername(username);
        String password = user.getPassword();

        int incorrectPasswordCount = user.getIncorrectPasswordCount();

        if (!password.equals(passwordInput)){
            user.setIncorrectPasswordCount(incorrectPasswordCount + 1);
            if (user.getIncorrectPasswordCount() >= Constants.MAX_ATTEMPT){
                // ban user
                user.setBan(true);
                // reset incorrect password count
                user.setIncorrectPasswordCount(0);
            }
            userRepo.save(user);
            return false;
        }

        // reset incorrect password count
        user.setIncorrectPasswordCount(0);
        return true;
    }

    public void changePassword(String username, String password){
        User user = userRepo.findByUsername(username);
        user.setPassword(password);
        userRepo.save(user);
    }

    public Boolean isBanned(String username){
        User user = userRepo.findByUsername(username);

        return user.getBan();
    }

    public static String rupiahFormat(Integer input){
        // Create a new Locale
        Locale id = new Locale("id", "ID");

        // Create a formatter given the Locale
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(id);

        // Format the Number into a Currency String
        return rupiahFormat.format(input);
    }
}