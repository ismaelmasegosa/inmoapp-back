package com.inmoapp.realtormanager.model.exception;

public class RequiredFieldsException extends RuntimeException {
    public RequiredFieldsException(String message) {
        super(message);
    }
}
