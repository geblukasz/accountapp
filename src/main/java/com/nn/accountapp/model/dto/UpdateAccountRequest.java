package com.nn.accountapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdateAccountRequest {

    @Schema(description = "Identification number of the account", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID identificationNumber;
    @Schema(description = "Amount for of currency to be bought", example = "10")
    private BigDecimal amountTo;
    @Schema(description = "Currency code of currency to be sold", example = "PLN")
    private AllowedCurrency currencyCodeFrom;
    @Schema(description = "Currency code of currency to be bought", example = "USD")
    private AllowedCurrency currencyCodeTo;

}
