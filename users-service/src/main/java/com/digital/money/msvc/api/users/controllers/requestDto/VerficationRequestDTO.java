package com.digital.money.msvc.api.users.controllers.requestDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerficationRequestDTO {

    private Integer verificationCode;
}
