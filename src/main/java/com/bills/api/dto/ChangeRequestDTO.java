package com.bills.api.dto;

import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

public class ChangeRequestDTO {

    @Range(min = 1)
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
