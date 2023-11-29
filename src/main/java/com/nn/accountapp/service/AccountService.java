package com.nn.accountapp.service;

import com.nn.accountapp.client.ExchangeRateProviderClient;
import com.nn.accountapp.model.dto.UpdateAccountDTO;
import com.nn.accountapp.model.dto.UpdateAccountResponse;
import com.nn.accountapp.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {


    private final AccountRepository accountRepository;
    private final ExchangeRateProviderClient exchangeRateProviderClient;

    public AccountService(AccountRepository accountRepository, ExchangeRateProviderClient exchangeRateProviderClient) {
        this.accountRepository = accountRepository;
        this.exchangeRateProviderClient = exchangeRateProviderClient;
    }

    public UpdateAccountResponse exchangeCurrency(UpdateAccountDTO updateAccountDTO) {
        return null;
    }
}
