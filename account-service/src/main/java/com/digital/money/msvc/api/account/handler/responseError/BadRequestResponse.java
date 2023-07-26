package com.digital.money.msvc.api.account.handler.responseError;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
public class BadRequestResponse {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public BadRequestResponse(String message, String path) {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.status = 400;
        this.error = "Bad Request";
        this.message = message;
        this.path = path;
    }
}

