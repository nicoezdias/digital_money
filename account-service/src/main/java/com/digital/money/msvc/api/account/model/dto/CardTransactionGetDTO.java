package com.digital.money.msvc.api.account.model.dto;

import com.digital.money.msvc.api.account.model.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CardTransactionGetDTO {

    private Long transactionId;

    private Double amount;

    private LocalDateTime realizationDate;

    private String description;

    private String cardNumber;

    private String toCvu;

    private TransactionType type;
}
