package com.nn.accountapp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAccountRequest {

    @Schema(description = "First name of the account holder", example = "John")
    private String firstName;

    @Schema(description = "Last name of the account holder", example = "Doe")
    private String lastName;

    @Schema(description = "Amount for account in PLN", example = "123.45")
    private BigDecimal amount;

}
