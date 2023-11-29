
package com.nn.accountapp.model.exchange.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Rate {

    private BigDecimal ask;
    private BigDecimal bid;
    private String effectiveDate;
    private String no;

}
