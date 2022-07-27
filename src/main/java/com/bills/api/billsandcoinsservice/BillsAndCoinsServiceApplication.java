package com.bills.api.billsandcoinsservice;

import com.bills.api.util.CoinConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.bills.api.*")
@EnableConfigurationProperties(CoinConfig.class)
public class BillsAndCoinsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillsAndCoinsServiceApplication.class, args);
	}

}
