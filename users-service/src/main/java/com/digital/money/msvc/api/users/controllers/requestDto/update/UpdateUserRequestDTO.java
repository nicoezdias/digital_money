package com.digital.money.msvc.api.users.controllers.requestDto.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(max = 30, message = "maximum number of characters 30")
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("last_name")
    @Size(max = 40, message = "maximum number of characters 40")
    private String lastName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long dni;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Email
    @Size(max = 60, message = "maximum number of characters 60")
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 8, max = 30, message = "minimum number of characters 8, maximum number of characters 30")
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    //@NotNull(message = "The phone cannot be null or empty ")
    private Integer phone;
}
