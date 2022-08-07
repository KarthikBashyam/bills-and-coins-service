package com.bills.api.billsandcoinsservice;

import com.bills.api.controller.ChangeCalculatorController;
import static org.assertj.core.api.Assertions.*;

import com.bills.api.domain.Coin;
import com.bills.api.dto.ChangeRequestDTO;
import com.bills.api.dto.ChangeResponseDTO;
import com.bills.api.exceptions.APIError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;


/**
 * Integration tests should cover happy paths.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BillsAndCoinsServiceIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ChangeCalculatorController controller;
	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

	@Test
	public void validateChangeServiceRequest() {
		ChangeRequestDTO requestDTO = new ChangeRequestDTO();
		requestDTO.setAmount(BigDecimal.ZERO);

		HttpEntity<ChangeRequestDTO> request = new HttpEntity<>(requestDTO, getHttpHeaders());

		APIError apiError = this.restTemplate.postForObject("http://localhost:" + port + "/calculate",
				requestDTO, APIError.class);

		assertThat(apiError.getReason()).contains("Validation failed");
	}

	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return headers;
	}

	@Test
	public void coinBalanceShouldContainCoinType() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/coins/balance",
				String.class)).contains("DIME");

	}

	@Test
	public void validateChangeServiceResponse() {
		ChangeRequestDTO requestDTO = new ChangeRequestDTO();
		requestDTO.setAmount(BigDecimal.valueOf(30));

		HttpEntity<ChangeRequestDTO> request = new HttpEntity<>(requestDTO, getHttpHeaders());

		ChangeResponseDTO changeRespoonseDTO = this.restTemplate.postForObject("http://localhost:" + port + "/calculate",
				requestDTO, ChangeResponseDTO.class);

		Map<Coin, Integer> coinsDenominations = changeRespoonseDTO.getCoinDenominations();

		assertThat(coinsDenominations.get(Coin.QUARTER)).isEqualTo(Integer.valueOf(100));
		assertThat(coinsDenominations.get(Coin.DIME)).isEqualTo(Integer.valueOf(50));
	}

	@Test
	public void validateCoinNotAvailableException() {

		ChangeRequestDTO requestDTO = new ChangeRequestDTO();
		requestDTO.setAmount(BigDecimal.valueOf(42));
		HttpEntity<ChangeRequestDTO> request = new HttpEntity<>(requestDTO, getHttpHeaders());

		APIError apiError = this.restTemplate.postForObject("http://localhost:" + port + "/calculate",
				requestDTO, APIError.class);
		assertThat(apiError.getReason()).contains("Coins are not available");
	}
}

