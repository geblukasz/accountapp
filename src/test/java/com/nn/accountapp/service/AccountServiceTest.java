package com.nn.accountapp.service;


import com.nn.accountapp.client.ExchangeRateProviderClient;
import com.nn.accountapp.exception.AccountNotFoundException;
import com.nn.accountapp.exception.CurrencyNotFoundException;
import com.nn.accountapp.exception.NotEnoughMoneyException;
import com.nn.accountapp.mapper.AccountEntityToAccountDTOMapper;
import com.nn.accountapp.mapper.CreateAccountEntityToAccountResponseMapper;
import com.nn.accountapp.model.dto.UpdateAccountDTO;
import com.nn.accountapp.model.dto.UpdateAccountResponse;
import com.nn.accountapp.model.entity.AccountEntity;
import com.nn.accountapp.model.entity.SubAccountEntity;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import com.nn.accountapp.model.exchange.response.ExchangeCurrencyResponse;
import com.nn.accountapp.model.exchange.response.Rate;
import com.nn.accountapp.repository.AccountRepository;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static com.nn.accountapp.model.enumeration.AllowedCurrency.PLN;
import static com.nn.accountapp.model.enumeration.AllowedCurrency.USD;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final AllowedCurrency BASE_CURRENCY_CODE = PLN;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountEntityToAccountDTOMapper accountDtoToAccountEntityMapper;


    @Mock
    private CreateAccountEntityToAccountResponseMapper createAccountEntityToAccountResponseMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ExchangeRateProviderClient exchangeRateProviderClient;

    private UpdateAccountDTO createUpdateAccountDTO(BigDecimal amountFrom, AllowedCurrency currencyCodeFrom, AllowedCurrency currencyCodeTo) {
        UUID identificationNumber = UUID.randomUUID();
        return new UpdateAccountDTO(identificationNumber, amountFrom, currencyCodeFrom, currencyCodeTo);
    }

    private ExchangeCurrencyResponse createExchangeRate() {
        ExchangeCurrencyResponse exchangeRate = EnhancedRandom.random(ExchangeCurrencyResponse.class);
        Rate rate = new Rate();
        rate.setAsk(BigDecimal.valueOf(3.45));
        rate.setBid(BigDecimal.valueOf(3.35));
        exchangeRate.setRates(List.of(rate));
        return exchangeRate;
    }

    private AccountEntity createAccountEntity(AllowedCurrency currencyCodeFrom, AllowedCurrency currencyCodeTo, BigDecimal initialAmountFrom, BigDecimal initialAmountTo) {
        AccountEntity accountEntity = EnhancedRandom.random(AccountEntity.class);
        SubAccountEntity currencyAmountEntityFrom = new SubAccountEntity(1, accountEntity, initialAmountFrom, currencyCodeFrom);
        SubAccountEntity currencyAmountEntityTo = new SubAccountEntity(1, accountEntity, initialAmountTo, currencyCodeTo);
        accountEntity.setCurrencyAmounts(List.of(currencyAmountEntityFrom, currencyAmountEntityTo));
        return accountEntity;
    }

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        exchangeRateProviderClient = mock(ExchangeRateProviderClient.class);
        accountService = new AccountService(accountRepository, exchangeRateProviderClient);
    }

    @Test
    void testExchangeCurrency_whenExchangeRegularValues_shouldOk() throws NotEnoughMoneyException, AccountNotFoundException, CurrencyNotFoundException {
        // given
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(BigDecimal.valueOf(10), PLN, USD);
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();
        AccountEntity accountEntity = createAccountEntity(PLN, USD, BigDecimal.valueOf(123.45), BigDecimal.ZERO);

        // when
        when(accountRepository.findByIdentificationNumber(any(UUID.class))).thenReturn(Optional.of(accountEntity));
        when(exchangeRateProviderClient.getExchangeRate(anyString())).thenReturn(exchangeRate);
        UpdateAccountResponse result = accountService.exchangeCurrency(updateAccountDTO);

        // then
        assertNotNull(result);
    }

    @Test
    void testExchangeCurrency_whenAskingForTooMuchOtherCurrency_shouldThrow() throws NotEnoughMoneyException, AccountNotFoundException, CurrencyNotFoundException {
        // given
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(BigDecimal.valueOf(100), PLN, USD);
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();
        AccountEntity accountEntity = createAccountEntity(PLN, USD, BigDecimal.valueOf(123.45), BigDecimal.ZERO);

        // when
        when(accountRepository.findByIdentificationNumber(any(UUID.class))).thenReturn(Optional.of(accountEntity));
        when(exchangeRateProviderClient.getExchangeRate(anyString())).thenReturn(exchangeRate);

        // then
        assertThrows(NotEnoughMoneyException.class, () -> accountService.exchangeCurrency(updateAccountDTO));
    }

    @Test
    void testExchangeCurrency_whenAskingForTooMuchBaseCurrency_shouldThrow() {
        // given
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(BigDecimal.valueOf(100), PLN, USD);
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();
        AccountEntity accountEntity = createAccountEntity(PLN, USD, BigDecimal.valueOf(12.45), BigDecimal.ZERO);

        // when
        when(accountRepository.findByIdentificationNumber(any(UUID.class))).thenReturn(Optional.of(accountEntity));
        when(exchangeRateProviderClient.getExchangeRate(anyString())).thenReturn(exchangeRate);

        // then
        assertThrows(NotEnoughMoneyException.class, () -> accountService.exchangeCurrency(updateAccountDTO));
    }

    @Test
    void testExchangeCurrency_whenAskingForAccountThatDoesNotExsits_shouldThrow() {
        // given
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(BigDecimal.valueOf(100), PLN, USD);
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();

        // then
        assertThrows(AccountNotFoundException.class, () -> accountService.exchangeCurrency(updateAccountDTO));
    }


}
