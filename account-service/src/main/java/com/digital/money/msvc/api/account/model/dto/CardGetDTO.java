package com.digital.money.msvc.api.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardGetDTO {

    @JsonProperty("card_id")
    private Long cardId;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("cardNumber")
    private String cardNumber;

    @JsonProperty("cardHolder")
    private String cardHolder;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("cardNetwork")
    private String cardNetwork;

    @JsonProperty("cardType")
    private String cardType;
}
