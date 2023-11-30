package com.nn.accountapp.service;

import com.nn.accountapp.client.ExchangeRateProviderClient;
import com.nn.accountapp.exception.AccountNotFoundException;
import com.nn.accountapp.exception.CurrencyNotFoundException;
import com.nn.accountapp.exception.NotEnoughMoneyException;
import com.nn.accountapp.mapper.AccountEntityToAccountDTOMapper;
import com.nn.accountapp.mapper.CreateAccountEntityToAccountResponseMapper;
import com.nn.accountapp.model.dto.*;
import com.nn.accountapp.model.entity.AccountEntity;
import com.nn.accountapp.model.entity.SubAccountEntity;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import com.nn.accountapp.model.exchange.response.ExchangeCurrencyResponse;
import com.nn.accountapp.repository.AccountRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.nn.accountapp.model.enumeration.AllowedCurrency.PLN;

@Service
@Validated
@RequiredArgsConstructor
public class AccountService {

    AccountEntityToAccountDTOMapper accountDtoToAccountEntityMapper = AccountEntityToAccountDTOMapper.INSTANCE;
    CreateAccountEntityToAccountResponseMapper createAccountEntityToAccountResponseMapper = CreateAccountEntityToAccountResponseMapper.INSTANCE;
    private final AllowedCurrency BASE_CURRENCY_CODE = PLN;

    private final AccountRepository accountRepository;
    private final ExchangeRateProviderClient exchangeRateProviderClient;

    public CreateAccountResponse createAccount(@NotNull @Valid final AccountDTO accountDTO) {
        AccountEntity accountEntity = accountDtoToAccountEntityMapper.accountDtoToAccountEntity(accountDTO);
        accountEntity.setIdentificationNumber(UUID.randomUUID());
        SubAccountEntity subAccountEntity = SubAccountEntity.builder()
                .currencyCode(PLN)
                .amount(accountDTO.getAmount())
                .account(accountEntity)
                .build();
        accountEntity.setCurrencyAmounts(List.of(subAccountEntity));
        AccountEntity savedAccountEntity = accountRepository.save(accountEntity);
        return createAccountEntityToAccountResponseMapper.accountDtoToAccountEntity(savedAccountEntity);
    }

    public UpdateAccountResponse exchangeCurrency(@NotNull @Valid final UpdateAccountDTO updateAccountDTO) throws NotEnoughMoneyException, AccountNotFoundException, CurrencyNotFoundException {
        final UUID identificationNumber = updateAccountDTO.getIdentificationNumber();
        final AccountEntity accountEntity = accountRepository.findByIdentificationNumber(identificationNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account " + identificationNumber + " not found"));
        final AllowedCurrency currencyCodeFrom = updateAccountDTO.getCurrencyCodeFrom();
        boolean isSellBaseCurrency = false;
        AllowedCurrency currencyCodeForExchangeRate;
        if (currencyCodeFrom.equals(BASE_CURRENCY_CODE)) {
            currencyCodeForExchangeRate = updateAccountDTO.getCurrencyCodeTo();
            isSellBaseCurrency = true;
        } else {
            currencyCodeForExchangeRate = currencyCodeFrom;
        }
        final ExchangeCurrencyResponse exchangeRate = exchangeRateProviderClient.getExchangeRate(currencyCodeForExchangeRate.name());
        final BigDecimal exchangePrice = prepareExchangePrice(isSellBaseCurrency, exchangeRate);
        final CalculationDTO calculationDTO = CalculationDTO.builder()
                .updateAccountDTO(updateAccountDTO)
                .currencyCodeForExchangeRate(currencyCodeForExchangeRate)
                .accountEntity(accountEntity)
                .exchangePrice(exchangePrice)
                .isSellBaseCurrency(isSellBaseCurrency)
                .amountTo(updateAccountDTO.getAmountTo())
                .currencyCodeTo(updateAccountDTO.getCurrencyCodeTo())
                .build();
        performCalculations(calculationDTO);
        accountRepository.save(accountEntity);
        return new UpdateAccountResponse(accountEntity.getIdentificationNumber());
    }

    private void performCalculations(@NotNull final CalculationDTO calculationDTO) throws NotEnoughMoneyException, CurrencyNotFoundException {
        final AllowedCurrency currencyCodeForExchangeRate = calculationDTO.getCurrencyCodeForExchangeRate();
        final AccountEntity accountEntity = calculationDTO.getAccountEntity();
        final BigDecimal exchangePrice = calculationDTO.getExchangePrice();
        final BigDecimal amountTo = calculationDTO.getAmountTo();
        final AllowedCurrency currencyCodeTo = calculationDTO.getCurrencyCodeTo();
        final List<SubAccountEntity> subAccountEntities = accountEntity.getCurrencyAmounts();
        final SubAccountEntity baseSubaccount = subAccountEntities.stream()
                .filter(entity -> entity.getCurrencyCode().equals(BASE_CURRENCY_CODE))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException("Base currency not found"));
        addSubAccountIfRequired(currencyCodeTo, subAccountEntities, accountEntity);
        final SubAccountEntity otherSubaccount = subAccountEntities.stream()
                .filter(currencyAmountEntity -> currencyAmountEntity.getCurrencyCode().equals(currencyCodeForExchangeRate))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException(currencyCodeForExchangeRate + " currency not found"));
        final CalculationAccountDTO calculationAccountDTO = CalculationAccountDTO.builder()
                .amountFrom(amountTo)
                .exchangePrice(exchangePrice)
                .baseSubAccountEntity(baseSubaccount)
                .otherSubaccountEntity(otherSubaccount)
                .build();
        if (calculationDTO.isSellBaseCurrency()) {
            calculateSellBaseCurrency(calculationAccountDTO);
        } else {
            calculateSellOtherCurrency(calculationAccountDTO);
        }
    }

    private void calculateSellOtherCurrency(@NotNull final CalculationAccountDTO calculationAccountDTO) throws NotEnoughMoneyException {
        final BigDecimal amountFrom = calculationAccountDTO.getAmountFrom();
        final SubAccountEntity baseSubAccountEntity = calculationAccountDTO.getBaseSubAccountEntity();
        final SubAccountEntity otherSubaccountEntity = calculationAccountDTO.getOtherSubaccountEntity();
        final BigDecimal multipliedAmount = amountFrom.multiply(calculationAccountDTO.getExchangePrice()).setScale(2, RoundingMode.HALF_EVEN);
        if (otherSubaccountEntity.getAmount().compareTo(amountFrom) < 0) {
            throw new NotEnoughMoneyException("Not enough money");
        }
        baseSubAccountEntity.setAmount(baseSubAccountEntity.getAmount().add(multipliedAmount));
        otherSubaccountEntity.setAmount(otherSubaccountEntity.getAmount().subtract(amountFrom));
    }

    private void calculateSellBaseCurrency(@NotNull final CalculationAccountDTO calculationAccountDTO) throws NotEnoughMoneyException {
        final BigDecimal amountFrom = calculationAccountDTO.getAmountFrom();
        final SubAccountEntity baseSubaccountEntity = calculationAccountDTO.getBaseSubAccountEntity();
        final SubAccountEntity otherSubaccountEntity = calculationAccountDTO.getOtherSubaccountEntity();
        final BigDecimal multipliedAmount = amountFrom.multiply(calculationAccountDTO.getExchangePrice()).setScale(2, RoundingMode.HALF_EVEN);
        final BigDecimal baseCurrencyAmount = baseSubaccountEntity.getAmount();
        if (baseCurrencyAmount.compareTo(multipliedAmount) < 0) {
            throw new NotEnoughMoneyException("Not enough money");
        }
        baseSubaccountEntity.setAmount(baseCurrencyAmount.subtract(multipliedAmount));
        otherSubaccountEntity.setAmount(otherSubaccountEntity.getAmount().add(amountFrom));
    }

    private BigDecimal prepareExchangePrice(final boolean isSellBaseCurrency, @NotNull final ExchangeCurrencyResponse exchangeRate) {
        BigDecimal exchangePrice;
        if (isSellBaseCurrency) {
            exchangePrice = exchangeRate.getRates().get(0).getAsk();
        } else {
            exchangePrice = exchangeRate.getRates().get(0).getBid();
        }
        return exchangePrice;
    }

    private void addSubAccountIfRequired(@NotNull final AllowedCurrency currencyCodeTo, @NotNull final List<SubAccountEntity> subAccountEntities, @NotNull final AccountEntity accountEntity) {
        Optional<SubAccountEntity> subAccountEntityOptional = subAccountEntities.stream()
                .filter(currencyAmountEntity -> currencyAmountEntity.getCurrencyCode().equals(currencyCodeTo))
                .findFirst();
        if (subAccountEntityOptional.isEmpty()) {
            SubAccountEntity currencyAmountEntity = SubAccountEntity.builder()
                    .currencyCode(currencyCodeTo)
                    .amount(BigDecimal.ZERO)
                    .account(accountEntity)
                    .build();
            accountEntity.getCurrencyAmounts().add(currencyAmountEntity);
        }
    }
}

