package com.digital.money.msvc.api.account.model.projections;

import com.digital.money.msvc.api.account.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public interface GetLastCVUs {

    @JsonProperty("cvu")
    String getTo_Cvu();

    LocalDateTime getRealization_date();

    Long getAccount_id();
    TransactionType getType();


}
