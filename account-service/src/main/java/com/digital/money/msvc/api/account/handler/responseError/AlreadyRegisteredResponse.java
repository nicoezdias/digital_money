package com.digital.money.msvc.api.account.handler.responseError;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
public class AlreadyRegisteredResponse {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public AlreadyRegisteredResponse(String message, String path) {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.status = 409;
        this.error = "Already Registered";
        this.message = message;
        this.path = path;
    }
}
