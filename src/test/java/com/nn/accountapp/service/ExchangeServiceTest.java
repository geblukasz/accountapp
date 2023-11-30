package com.nn.accountapp.service;


import com.nn.accountapp.client.ExchangeRateProviderClient;
import com.nn.accountapp.exception.AccountNotFoundException;
import com.nn.accountapp.exception.CurrencyNotFoundException;
import com.nn.accountapp.exception.NotEnoughMoneyException;
import com.nn.accountapp.model.dto.UpdateAccountDTO;
import com.nn.accountapp.model.dto.UpdateAccountResponse;
import com.nn.accountapp.model.entity.AccountEntity;
import com.nn.accountapp.model.entity.SubAccountEntity;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import com.nn.accountapp.model.exchange.response.ExchangeCurrencyResponse;
import com.nn.accountapp.model.exchange.response.Rate;
import com.nn.accountapp.repository.AccountRepository;
import org.jeasy.random.EasyRandom;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

    private static final BigDecimal ASK_PRICE = BigDecimal.valueOf(3.45);
    private static final BigDecimal BID_PRICE = BigDecimal.valueOf(3.35);
    private static final BigDecimal BASE_CURRENCY_INITIAL_VALUE = BigDecimal.valueOf(123.45);
    private static final BigDecimal INITIAL_VALUE_OF_OTHER_CURRENCY = BigDecimal.valueOf(20);

    @InjectMocks
    private ExchangeService exchangeService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private ExchangeRateProviderClient exchangeRateProviderClient;

    private UpdateAccountDTO createUpdateAccountDTO(BigDecimal amountFrom) {
        UUID identificationNumber = UUID.randomUUID();
        return new UpdateAccountDTO(identificationNumber, amountFrom, PLN, USD);
    }

    private UpdateAccountDTO createUpdateAccountDTO(BigDecimal amountFrom, AllowedCurrency from, AllowedCurrency to) {
        UUID identificationNumber = UUID.randomUUID();
        return new UpdateAccountDTO(identificationNumber, amountFrom, from, to);
    }

    private ExchangeCurrencyResponse createExchangeRate() {
        ExchangeCurrencyResponse exchangeRate = new EasyRandom().nextObject(ExchangeCurrencyResponse.class);
        Rate rate = new Rate();
        rate.setAsk(ASK_PRICE);
        rate.setBid(BID_PRICE);
        exchangeRate.setRates(List.of(rate));
        return exchangeRate;
    }

    private AccountEntity createAccountEntity(BigDecimal initialAmountFrom) {
        AccountEntity accountEntity = new EasyRandom().nextObject(AccountEntity.class);
        SubAccountEntity currencyAmountEntityFrom = new SubAccountEntity(1, accountEntity, initialAmountFrom, PLN);
        SubAccountEntity currencyAmountEntityTo = new SubAccountEntity(1, accountEntity, INITIAL_VALUE_OF_OTHER_CURRENCY, USD);
        accountEntity.setSubAccounts(List.of(currencyAmountEntityFrom, currencyAmountEntityTo));
        return accountEntity;
    }

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        exchangeRateProviderClient = mock(ExchangeRateProviderClient.class);
        exchangeService = new ExchangeService(accountRepository, exchangeRateProviderClient, accountService);
    }

    @Test
    void testExchangeCurrency_whenExchangeBaseCurrencyToOtherCurrency_shouldOk() throws NotEnoughMoneyException, AccountNotFoundException, CurrencyNotFoundException {
        // given
        BigDecimal OTHER_CURRENCY_AMOUNT = BigDecimal.TEN;
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(OTHER_CURRENCY_AMOUNT);
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();
        AccountEntity accountEntity = createAccountEntity(BASE_CURRENCY_INITIAL_VALUE);

        // when
        when(accountRepository.findByIdentificationNumber(any(UUID.class))).thenReturn(Optional.of(accountEntity));
        when(exchangeRateProviderClient.getExchangeRate(anyString())).thenReturn(exchangeRate);
        UpdateAccountResponse result = exchangeService.exchangeCurrency(updateAccountDTO);

        // then
        assertNotNull(result);
        assertNotNull(result.getIdentificationNumber());
        assertEquals(BASE_CURRENCY_INITIAL_VALUE.subtract(OTHER_CURRENCY_AMOUNT.multiply(ASK_PRICE)), getBaseCurrencySubAccountEntity(accountEntity).getAmount());
        assertEquals(OTHER_CURRENCY_AMOUNT.add(INITIAL_VALUE_OF_OTHER_CURRENCY), getOtherCurrencySubAccountEntity(accountEntity).getAmount());
    }

    @Test
    void testExchangeCurrency_whenExchangeOtherCurrencyToBaseCurrency_shouldOk() throws NotEnoughMoneyException, AccountNotFoundException, CurrencyNotFoundException {
        // given
        BigDecimal OTHER_CURRENCY_AMOUNT = BigDecimal.TEN;
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(OTHER_CURRENCY_AMOUNT, USD, PLN);
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();
        AccountEntity accountEntity = createAccountEntity(BASE_CURRENCY_INITIAL_VALUE);

        // when
        when(accountRepository.findByIdentificationNumber(any(UUID.class))).thenReturn(Optional.of(accountEntity));
        when(exchangeRateProviderClient.getExchangeRate(anyString())).thenReturn(exchangeRate);
        UpdateAccountResponse result = exchangeService.exchangeCurrency(updateAccountDTO);

        // then
        assertNotNull(result);
        assertNotNull(result.getIdentificationNumber());
        assertEquals(BASE_CURRENCY_INITIAL_VALUE.add(OTHER_CURRENCY_AMOUNT.multiply(BID_PRICE)), getBaseCurrencySubAccountEntity(accountEntity).getAmount());
        assertEquals(INITIAL_VALUE_OF_OTHER_CURRENCY.subtract(OTHER_CURRENCY_AMOUNT), getOtherCurrencySubAccountEntity(accountEntity).getAmount());
    }

    private static SubAccountEntity getOtherCurrencySubAccountEntity(AccountEntity accountEntity) {
        return accountEntity.getSubAccounts().stream()
                .filter(subAccount -> subAccount.getCurrencyCode().equals(USD))
                .findAny()
                .orElseThrow();
    }

    private static SubAccountEntity getBaseCurrencySubAccountEntity(AccountEntity accountEntity) {
        return accountEntity.getSubAccounts().stream()
                .filter(subAccount -> subAccount.getCurrencyCode().equals(PLN))
                .findAny()
                .orElseThrow();
    }

    @Test
    void testExchangeCurrency_whenAskingForTooMuchOtherCurrency_shouldThrow() {
        // given
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(BigDecimal.valueOf(100));
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();
        AccountEntity accountEntity = createAccountEntity(BASE_CURRENCY_INITIAL_VALUE);

        // when
        when(accountRepository.findByIdentificationNumber(any(UUID.class))).thenReturn(Optional.of(accountEntity));
        when(exchangeRateProviderClient.getExchangeRate(anyString())).thenReturn(exchangeRate);

        // then
        assertThrows(NotEnoughMoneyException.class, () -> exchangeService.exchangeCurrency(updateAccountDTO));
    }

    @Test
    void testExchangeCurrency_whenAskingForTooMuchBaseCurrency_shouldThrow() {
        // given
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(BigDecimal.valueOf(100));
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();
        AccountEntity accountEntity = createAccountEntity(BigDecimal.valueOf(12.45));

        // when
        when(accountRepository.findByIdentificationNumber(any(UUID.class))).thenReturn(Optional.of(accountEntity));
        when(exchangeRateProviderClient.getExchangeRate(anyString())).thenReturn(exchangeRate);

        // then
        assertThrows(NotEnoughMoneyException.class, () -> exchangeService.exchangeCurrency(updateAccountDTO));
    }

    @Test
    void testExchangeCurrency_whenAskingForAccountThatDoesNotExsits_shouldThrow() {
        // given
        UpdateAccountDTO updateAccountDTO = createUpdateAccountDTO(BigDecimal.valueOf(100));
        ExchangeCurrencyResponse exchangeRate = createExchangeRate();

        // then
        assertThrows(AccountNotFoundException.class, () -> exchangeService.exchangeCurrency(updateAccountDTO));
    }

}
