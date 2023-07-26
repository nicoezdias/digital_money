package com.digital.money.msvc.api.account.handler.responseError;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
public class AmountOfMoneyResponse {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public AmountOfMoneyResponse(String message, String path) {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.status = 410;
        this.error = "Insufficient Funds";
        this.message = message;
        this.path = path;
    }
}
