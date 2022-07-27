package com.bills.api.exceptions;

public class CoinNotAvailableException extends Exception {

    public CoinNotAvailableException(String message) {
        super(message);
    }
}
