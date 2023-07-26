package com.digital.money.msvc.api.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountPostDto {

    @JsonProperty("user_id")
    @NotNull(message = "The user_id cannot be null")
    @NotEmpty(message = "The user_id cannot be empty")
    private Long userId;
}
