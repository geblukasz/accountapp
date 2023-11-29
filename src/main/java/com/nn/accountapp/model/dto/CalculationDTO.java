package com.nn.accountapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nn.accountapp.model.entity.AccountEntity;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationDTO {

    private UpdateAccountDTO updateAccountDTO;
    private AllowedCurrency currencyCodeForExchangeRate;
    private AllowedCurrency currencyCodeTo;
    private AccountEntity accountEntity;
    private BigDecimal exchangePrice;
    private BigDecimal amountTo;
    private boolean isSellBaseCurrency;

}
