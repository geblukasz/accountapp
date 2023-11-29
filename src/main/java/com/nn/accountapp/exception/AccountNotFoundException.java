package com.nn.accountapp.exception;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
