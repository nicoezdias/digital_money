package com.digital.money.msvc.api.users.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @JsonProperty("role_id")
    @Column(unique = true, name = "role_id")
    private Integer roleId;

    @Column(unique = true, nullable = false, length = 10)
    private String name;
}
