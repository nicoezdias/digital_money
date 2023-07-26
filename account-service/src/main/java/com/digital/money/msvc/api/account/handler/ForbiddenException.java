package com.digital.money.msvc.api.account.handler;

public class ForbiddenException extends Exception{
    public ForbiddenException(String message){
        super(message);
    }
}
