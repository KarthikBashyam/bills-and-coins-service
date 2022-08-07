package com.bills.api.billsandcoinsservice;

import com.bills.api.controller.ChangeCalculatorController;
import com.bills.api.domain.Coin;
import com.bills.api.dto.CoinBalanceResponseDTO;
import com.bills.api.service.ChangeCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.HashMap;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller Slice Test
 *
 */
@WebMvcTest(ChangeCalculatorController.class)
public class ChargeCalculatorControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChangeCalculatorService changeCalculatorService;

    @Test
    public void getCoinBalance_ShouldContainAllCoinTypes() throws Exception {

        var coinBalance = new HashMap<Coin, Integer>();
        for(Coin coin : Coin.values()) {
            coinBalance.put(coin, 100);
        }
        CoinBalanceResponseDTO coinBalanceResponseDTO = new CoinBalanceResponseDTO(coinBalance);

        when(changeCalculatorService.getCoinBalance()).thenReturn(coinBalanceResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/coins/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("coinBalance.QUARTER").value("100"));

    }

}