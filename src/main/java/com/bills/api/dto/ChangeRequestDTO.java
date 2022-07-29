package com.bills.api.dto;

import org.hibernate.validator.constraints.Range;

public class ChangeRequestDTO {

    @Range(min = 1)
    private double amount;

    private boolean requestForMoreCoins;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isRequestForMoreCoins() {
        return requestForMoreCoins;
    }

    public void setRequestForMoreCoins(boolean requestForMoreCoins) {
        this.requestForMoreCoins = requestForMoreCoins;
    }
}
