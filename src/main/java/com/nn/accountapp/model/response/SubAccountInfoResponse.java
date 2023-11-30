package com.nn.accountapp.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class SubAccountInfoResponse {

    private BigDecimal amount;
    private AllowedCurrency currencyCode;

}
