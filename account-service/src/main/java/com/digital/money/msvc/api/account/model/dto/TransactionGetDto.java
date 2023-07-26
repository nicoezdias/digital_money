package com.digital.money.msvc.api.account.model.dto;

import com.digital.money.msvc.api.account.model.Account;
import com.digital.money.msvc.api.account.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionGetDto {

    private Long transactionId;

    private Double amount;

    private LocalDateTime realizationDate;

    private String description;

    private String fromCvu;

    private String toCvu;

    private TransactionType type;

    @JsonIgnore
    private Account account;
}
