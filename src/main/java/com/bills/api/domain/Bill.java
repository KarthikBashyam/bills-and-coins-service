package com.bills.api.domain;

public enum Bill {


    ONE(1), TWO(2), FIVE(5), TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100);

    private int value;


    Bill(int billType) {
        this.value = billType;
    }

    public int getBillValue() {
        return value;
    }


}
