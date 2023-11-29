
package com.nn.accountapp.model.exchange.response;

import lombok.Data;

import java.util.List;

@Data
public class ExchangeCurrencyResponse {

    private String code;
    private String currency;
    private List<Rate> rates;
    private String table;

}
