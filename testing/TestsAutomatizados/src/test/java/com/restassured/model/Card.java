package com.restassured.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    private String alias;
    private Long cardNumber;
    private String cardHolder;
    private String expirationDate;
    private Integer cvv;
    private String bank;
    private String cardType;
    //private Double cardBalance;

}
