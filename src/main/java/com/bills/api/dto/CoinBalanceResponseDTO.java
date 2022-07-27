package com.bills.api.dto;

import com.bills.api.domain.Coin;

import java.util.Map;

public class CoinBalanceResponseDTO {

    private Map<Coin, Integer> coinBalance;

    public CoinBalanceResponseDTO(Map<Coin, Integer> coinBalance) {
        this.coinBalance = coinBalance;
    }

    public Map<Coin, Integer> getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(Map<Coin, Integer> coinBalance) {
        this.coinBalance = coinBalance;
    }
}
