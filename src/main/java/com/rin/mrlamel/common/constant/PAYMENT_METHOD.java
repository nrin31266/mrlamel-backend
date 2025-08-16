package com.rin.mrlamel.common.constant;

public enum PAYMENT_METHOD {
    CASH("Cash"),
    BANK_TRANSFER("Bank Transfer");

    private final String method;

    PAYMENT_METHOD(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
