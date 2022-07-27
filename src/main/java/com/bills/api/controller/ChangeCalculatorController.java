package com.bills.api.controller;

import com.bills.api.domain.Coin;
import com.bills.api.dto.ChangeRequestDTO;
import com.bills.api.dto.ChangeResponseDTO;
import com.bills.api.dto.CoinBalanceResponseDTO;
import com.bills.api.exceptions.CoinNotAvailableException;
import com.bills.api.service.ChangeCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class ChangeCalculatorController {

    @Autowired
    private ChangeCalculatorService changeCalculatorService;

    @PostMapping("/calculate")
    public ChangeResponseDTO calculateChange(@Valid @RequestBody ChangeRequestDTO requestDTO) throws CoinNotAvailableException {
        return changeCalculatorService.calculate(requestDTO.getAmount());
    }

    @GetMapping("/coins/balance")
    public CoinBalanceResponseDTO getCoinBalance() {
        return changeCalculatorService.getCoinBalance();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Welcome";
    }

}
