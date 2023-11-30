package com.nn.accountapp.service;

import com.nn.accountapp.model.dto.AccountDTO;
import com.nn.accountapp.model.dto.CreateAccountResponse;
import com.nn.accountapp.model.entity.AccountEntity;
import com.nn.accountapp.model.entity.SubAccountEntity;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import com.nn.accountapp.repository.AccountRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void testCreateAccount() {
        // given
        AccountDTO accountDTO = new EasyRandom().nextObject(AccountDTO.class);
        AccountEntity accountEntity = AccountEntity.builder().build();

        // when
        when(accountRepository.save(any())).thenReturn(accountEntity);
        CreateAccountResponse result = accountService.createAccount(accountDTO);

        // then
        verify(accountRepository, times(1)).save(any());
    }

    @Test
    void testAddSubAccountIfRequired_SubAccountNotPresent() {
        // given
        AllowedCurrency currencyCodeTo = AllowedCurrency.PLN;
        List<SubAccountEntity> subAccountEntities = new ArrayList<>();
        AccountEntity accountEntity = AccountEntity.builder().build();
        accountEntity.setSubAccounts(subAccountEntities);

        // when
        accountService.addSubAccountIfRequired(currencyCodeTo, subAccountEntities, accountEntity);

        // then
        assertEquals(1, accountEntity.getSubAccounts().size());
        assertEquals(currencyCodeTo, accountEntity.getSubAccounts().get(0).getCurrencyCode());
    }

    @Test
    void testAddSubAccountIfRequired_SubAccountPresent() {
        // given
        AllowedCurrency currencyCodeTo = AllowedCurrency.PLN;
        SubAccountEntity existingSubAccount = SubAccountEntity.builder().currencyCode(currencyCodeTo).build();
        List<SubAccountEntity> subAccountEntities = Collections.singletonList(existingSubAccount);
        AccountEntity accountEntity = AccountEntity.builder().subAccounts(subAccountEntities).build();

        // when
        accountService.addSubAccountIfRequired(currencyCodeTo, subAccountEntities, accountEntity);

        // then
        assertEquals(1, accountEntity.getSubAccounts().size());
        assertEquals(existingSubAccount, accountEntity.getSubAccounts().get(0));
    }

}
