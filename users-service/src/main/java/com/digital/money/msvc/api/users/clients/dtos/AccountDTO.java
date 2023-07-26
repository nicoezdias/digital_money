package com.digital.money.msvc.api.users.clients.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("account_id")
    private Integer accountId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alias;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cvu;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double availableBalance;
}
