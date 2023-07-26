package com.digital.money.msvc.api.account.handler.responseError;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
public class NotFoundResponse {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public NotFoundResponse(String message, String path) {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.status = 404;
        this.error = "Not Found";
        this.message = message;
        this.path = path;
    }
}