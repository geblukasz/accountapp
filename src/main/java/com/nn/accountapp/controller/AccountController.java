package com.nn.accountapp.controller;

import com.nn.accountapp.exception.AccountNotFoundException;
import com.nn.accountapp.exception.CurrencyNotFoundException;
import com.nn.accountapp.exception.NotEnoughMoneyException;
import com.nn.accountapp.mapper.CreateAccountRequestToAccountDTO;
import com.nn.accountapp.mapper.UpdateAccountRequestToUpdateAccountDTO;
import com.nn.accountapp.model.dto.*;
import com.nn.accountapp.service.AccountService;
import com.nn.accountapp.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private CreateAccountRequestToAccountDTO createAccountRequestToAccountDTOMapper = CreateAccountRequestToAccountDTO.INSTANCE;
    private UpdateAccountRequestToUpdateAccountDTO updateAccountRequestToUpdateAccountDTO = UpdateAccountRequestToUpdateAccountDTO.INSTANCE;

    private final AccountService accountService;
    private final ExchangeService exchangeService;

    public AccountController(AccountService accountService, ExchangeService exchangeService) {
        this.accountService = accountService;
        this.exchangeService = exchangeService;
    }

    @PostMapping("/")
    @Operation(summary = "Create account", description = "Create account with initial PLN amount")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody @Valid final CreateAccountRequest createAccountRequest) {
        AccountDTO accountDTO = createAccountRequestToAccountDTOMapper.accountDtoToAccountEntity(createAccountRequest);
        CreateAccountResponse response = accountService.createAccount(accountDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/")
    @Operation(summary = "Update account", description = "Update account using identification number received when creating an account. " +
            "AmountTo variable is the amount of currencyCodeTo to be bought for currencyCodeFrom currency." +
            "For example: amountTo: 10, currencyCodeFrom: PLN, currencyCodeTo: USD means \"Buy 10 USD for X PLN\"")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UpdateAccountResponse> updateAccount(@RequestHeader(defaultValue = "6f323a5b-083a-42f7-a775-2fe75156bf4e") UUID identificationNumber,
                                                               @RequestBody @Valid final UpdateAccountRequest updateAccountRequest)
            throws NotEnoughMoneyException, AccountNotFoundException, CurrencyNotFoundException {
        final UpdateAccountDTO updateAccountDTO = updateAccountRequestToUpdateAccountDTO.accountDtoToAccountEntity(updateAccountRequest, identificationNumber);
        final UpdateAccountResponse response = exchangeService.exchangeCurrency(updateAccountDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
