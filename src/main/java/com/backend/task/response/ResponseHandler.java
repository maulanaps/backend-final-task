package com.backend.task.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class ResponseHandler {
    public static ResponseEntity<Object> createResponse(HttpStatus httpStatus, String message) {
        HashMap<String, Object> response = new HashMap<>();

        response.put("status", httpStatus.value());
        response.put("message", message);

        return new ResponseEntity<>(response, httpStatus);
    }
}