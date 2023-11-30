package com.nn.accountapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nn.accountapp.model.entity.SubAccountEntity;
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
public class CalculationAccountDTO {

    private BigDecimal amountFrom;
    private BigDecimal exchangePrice;
    private SubAccountEntity baseSubAccountEntity;
    private SubAccountEntity otherSubaccountEntity;

}
