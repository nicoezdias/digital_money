package com.digital.money.msvc.api.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionPostDto {
    @NotNull(message = "The amount cannot be null")
    private Double amount;

    @JsonProperty("description")
    @NotNull(message = "The description cannot be null")
    @NotEmpty(message = "The description cannot be empty")
    @Size(min = 5, max = 50, message = "Please enter a brief description for the transaction (between 5 to 50 characters)")
    private String description;

    @JsonProperty("from_account")
    @NotNull(message = "The account from your account cannot be null")
    @NotEmpty(message = "The account from your account cannot be empty")
    @Size(min = 14, max = 39, message = "The account from which you want to send that you have entered does not comply with the alias, cvu or cbu rules")
    private String fromAccount;

    @JsonProperty("to_account")
    @NotNull(message = "The account to which you trying to send cannot be null")
    @NotEmpty(message = "The account to which you trying to send cannot be empty")
    @Size(min = 5, max = 39, message = "The account to which you are sending that you have entered does not comply with the alias, cvu or cbu rules")
    private String toAccount;

}
