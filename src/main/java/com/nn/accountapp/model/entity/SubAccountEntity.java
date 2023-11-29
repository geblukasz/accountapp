package com.nn.accountapp.model.entity;


import com.nn.accountapp.model.enumeration.AllowedCurrency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SUBACCOUNT")
public class SubAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AllowedCurrency currencyCode;

}
