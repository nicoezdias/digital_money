package com.digital.money.msvc.api.account.service;

public interface IService<T,K>{
    K save(T t);
}