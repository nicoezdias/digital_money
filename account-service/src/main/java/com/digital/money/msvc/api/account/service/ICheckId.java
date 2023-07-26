package com.digital.money.msvc.api.account.service;

import com.digital.money.msvc.api.account.handler.ResourceNotFoundException;

public interface ICheckId<T>{
    String msjIdError = "The search returned no results with";
    T checkId(Long c) throws ResourceNotFoundException;
}
