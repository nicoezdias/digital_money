package com.digital.money.msvc.api.account.handler.responseError;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
public class ForbiddenResponse {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ForbiddenResponse(String message, String path) {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.status = 403;
        this.error = "Forbidden";
        this.message = message;
        this.path = path;
    }
}
