package com.nn.accountapp.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class AccountDTO {

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private BigDecimal amount;

}
