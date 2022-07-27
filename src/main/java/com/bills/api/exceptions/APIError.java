package com.bills.api.exceptions;

public class APIError {

    private String reason;

    public APIError(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "APIError{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
