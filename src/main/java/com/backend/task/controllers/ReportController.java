package com.backend.task.controllers;

import com.backend.task.dto.ReportDto2;
import com.backend.task.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportServices reportServices;

    @GetMapping("/getreport/{date}")
    ResponseEntity<Object> getreport(@PathVariable LocalDate date){

        return new ResponseEntity<>(new ReportDto2(reportServices.getReports2(date)), HttpStatus.OK);
    }
}
