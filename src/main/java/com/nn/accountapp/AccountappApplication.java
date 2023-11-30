package com.nn.accountapp;

import com.nn.accountapp.model.enumeration.AllowedCurrency;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Currency;

@EnableFeignClients
@SpringBootApplication
public class AccountappApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountappApplication.class, args);
	}

	@PostConstruct
	public void validateAllowedCurrencies() {
		AllowedCurrency[] values = AllowedCurrency.values();
		for (AllowedCurrency value : values) {
			try {
				Currency.getAvailableCurrencies().contains(Currency.getInstance(value.name()));
			} catch (IllegalArgumentException illegalArgumentException) {
				throw new IllegalArgumentException("AllowedCurrency enum value " + value.name() + " is not valid");
			}
		}
	}

}
