package com.msvc.auth.server.sprgbt.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleDTO {

    @JsonProperty("role_id")
    private int roleId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
}
