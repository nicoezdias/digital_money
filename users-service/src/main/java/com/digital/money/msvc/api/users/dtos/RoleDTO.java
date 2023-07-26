package com.digital.money.msvc.api.users.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

    @JsonProperty("role_id")
    private int roleId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
}
