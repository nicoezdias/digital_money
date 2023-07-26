package com.digital.money.msvc.api.users.controllers.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewPassDTO {

    @NotBlank(message = "The password cannot be null or empty ")
    @Size(min = 8, max = 30, message = "minimum number of characters 8, maximum number of characters 30")
    private String pass;

    @NotBlank(message = "The password cannot be null or empty ")
    @Size(min = 8, max = 30, message = "minimum number of characters 8, maximum number of characters 30")
    private String passRep;

}
