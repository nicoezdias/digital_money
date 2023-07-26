package com.digital.money.msvc.api.account.model.dto;

import com.digital.money.msvc.api.account.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListTransactionDto {

    private AccountGetDto account;
    List<Transaction> transactions;

}
