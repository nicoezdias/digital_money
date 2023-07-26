package com.digital.money.msvc.api.account.handler;

public class AlreadyRegisteredException extends Exception {
    public AlreadyRegisteredException(String message){
        super(message);
    }
}
