package com.nn.accountapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateAccountDTO {

    private UUID identificationNumber;
    private BigDecimal amountTo;
    private AllowedCurrency currencyCodeFrom;
    private AllowedCurrency currencyCodeTo;

}
