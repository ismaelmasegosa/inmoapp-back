package com.inmoapp.realestate.controller;

import com.inmoapp.realestate.model.exception.RealEstateNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(RealEstateNotFound.class)
    private ResponseEntity realEstateNotFound(RealEstateNotFound ex){
        return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}