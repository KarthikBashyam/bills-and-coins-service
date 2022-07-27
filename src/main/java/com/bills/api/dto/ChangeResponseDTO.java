package com.bills.api.dto;

import com.bills.api.domain.Bill;
import com.bills.api.domain.Coin;

import java.util.Map;

public class ChangeResponseDTO {

    private Map<Bill, Integer> billsDenominations;

    private Map<Coin, Integer> coinDenominations;

    public Map<Bill, Integer> getBillsDenominations() {
        return billsDenominations;
    }

    public void setBillsDenominations(Map<Bill, Integer> billsDenominations) {
        this.billsDenominations = billsDenominations;
    }

    public Map<Coin, Integer> getCoinDenominations() {
        return coinDenominations;
    }

    public void setCoinDenominations(Map<Coin, Integer> coinDenominations) {
        this.coinDenominations = coinDenominations;
    }
}
