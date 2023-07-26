package com.digital.money.msvc.api.users.exceptions;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String mensaje){
        super(mensaje);
    }
}