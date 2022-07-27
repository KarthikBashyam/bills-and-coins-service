package com.bills.api.domain;

import java.math.BigDecimal;

public enum Bill {


    ONE(new BigDecimal(1)),
    TWO(new BigDecimal(2)),
    FIVE(new BigDecimal(5)),
    TEN(new BigDecimal(10)),
    TWENTY(new BigDecimal(20)),
    FIFTY(new BigDecimal(50)),
    HUNDRED(new BigDecimal(100));

    private BigDecimal value;


    Bill(BigDecimal billValue) {
        this.value = billValue;
    }

    public BigDecimal getBillValue() {
        return value;
    }


}
