package com.bills.api.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Coin {

    CENT(new BigDecimal(0.01).setScale(2,RoundingMode.FLOOR)),
    NICKEL(new BigDecimal(0.05).setScale(2, RoundingMode.FLOOR)),
    DIME(new BigDecimal(0.10).setScale(2, RoundingMode.FLOOR)),
    QUARTER(new BigDecimal(0.25).setScale(2,RoundingMode.FLOOR));
    BigDecimal value;

    private Coin(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getCoinValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Coin{" + this.name()+"("
                +"value=" + value+")" +
                '}';
    }
}

