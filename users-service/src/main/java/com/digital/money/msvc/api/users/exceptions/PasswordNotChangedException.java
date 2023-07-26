package com.digital.money.msvc.api.users.exceptions;

public class PasswordNotChangedException extends Exception{
    public PasswordNotChangedException(String mensaje){
        super(mensaje);
    }
}
