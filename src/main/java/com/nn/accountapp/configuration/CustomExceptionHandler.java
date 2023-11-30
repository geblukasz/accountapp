package com.nn.accountapp.configuration;

import com.nn.accountapp.exception.AccountNotFoundException;
import com.nn.accountapp.exception.CurrencyNotFoundException;
import com.nn.accountapp.exception.NotEnoughMoneyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR = "error";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";

    @ExceptionHandler(NotEnoughMoneyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleNotEnoughMoneyException(NotEnoughMoneyException notEnoughMoneyException, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(MESSAGE, "Not enough money to perform the operation");
        log.error("[handleNotEnoughMoneyException] Not enough money to perform the operation", notEnoughMoneyException);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleCurrencyNotFoundException(CurrencyNotFoundException currencyNotFoundException, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put(MESSAGE, "Currency not available");
        log.error("[handleCurrencyNotFoundException] Currency not available", currencyNotFoundException);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException accountNotFoundException, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.NOT_FOUND.value());
        body.put(ERROR, HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put(MESSAGE, "Account not found");
        log.error("[handleAccountNotFound] Account not found", accountNotFoundException);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception exception, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put(MESSAGE, exception.getMessage());
        log.error("[handleGeneralException] Internal server error", exception);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
