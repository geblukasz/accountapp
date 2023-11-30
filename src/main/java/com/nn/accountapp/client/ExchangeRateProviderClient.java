package com.nn.accountapp.client;

import com.nn.accountapp.configuration.ExternalProviderCommunicationConfiguration;
import com.nn.accountapp.model.exchange.response.ExchangeCurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "exchangeRateProviderClient",
        url = "${external-api.nbp.address}",
        configuration = ExternalProviderCommunicationConfiguration.class)
public interface ExchangeRateProviderClient {

    @GetMapping(value = "/api/exchangerates/rates/C/{targetCurrency}?format=JSON")
    ExchangeCurrencyResponse getExchangeRate(@PathVariable("targetCurrency") String baseCurrency);

}
