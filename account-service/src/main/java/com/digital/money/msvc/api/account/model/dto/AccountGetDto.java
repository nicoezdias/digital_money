package com.digital.money.msvc.api.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AccountGetDto {

    @JsonProperty("account_id")
    private Long accountId;

    private String alias;

    private String cvu;

    private double availableBalance;

}
