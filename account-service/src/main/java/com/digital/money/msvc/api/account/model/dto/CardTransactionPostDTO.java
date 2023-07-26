package com.digital.money.msvc.api.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CardTransactionPostDTO {

    @JsonProperty("amount")
    @NotNull(message = "The amount cannot be empty")
    @Digits(integer = 13, fraction = 2, message = "The amount must be a number with two decimal places")
    private Double amount;

    @JsonProperty("cardId")
    @NotNull(message = "The card cannot be null")
    private Long cardId;
}
