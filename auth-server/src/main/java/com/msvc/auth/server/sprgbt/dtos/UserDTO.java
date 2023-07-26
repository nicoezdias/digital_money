package com.msvc.auth.server.sprgbt.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @JsonProperty("user_id")
    private Long userId;

    private String name;

    @JsonProperty("last_name")
    private String lastName;

    private String cvu;

    private String alias;

    private Long dni;

    private Integer accountId;

    private String email;

    private String password;

    private Integer phone;

    private boolean enabled;

    private int attempts;

    private Boolean verified;

    private RoleDTO role;
}
