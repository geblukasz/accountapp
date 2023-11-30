package com.nn.accountapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdateAccountRequest {

    @Schema(description = "Amount for of currency to be bought", example = "10")
    private BigDecimal amountTo;
    @Schema(description = "Currency code of currency to be sold", example = "PLN")
    private AllowedCurrency currencyCodeFrom;
    @Schema(description = "Currency code of currency to be bought", example = "USD")
    private AllowedCurrency currencyCodeTo;

}
