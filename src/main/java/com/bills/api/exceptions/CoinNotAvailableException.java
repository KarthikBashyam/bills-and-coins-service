package com.bills.api.exceptions;

//Extend RuntimeException instead of Exception. Don't throw checked exceptions
public class CoinNotAvailableException extends RuntimeException {

    public CoinNotAvailableException(String message) {
        super(message);
    }
}
