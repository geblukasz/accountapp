package com.nn.accountapp.exception;

public class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException(String errorMessage) {
        super(errorMessage);
    }

}
