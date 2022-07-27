package com.bills.api.dto;

import org.hibernate.validator.constraints.Range;

public class ChangeRequestDTO {

    @Range(min = 1)
    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
