package com.digital.money.msvc.api.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardPostDTO {

    @JsonProperty("alias")
    @NotBlank(message = "The alias cannot be empty")
    @Pattern(message = "The alias must only contain letter and spaces"
            , regexp = "^[a-zA-Z\\s\\~\\´\\á\\é\\í\\ó\\ú\\ñ\\Á\\É\\Í\\Ó\\Ú\\Ñ]*$")
    @Size(max = 30, message = "The alias must not exceed 30 characters")
    @Size(min = 3,  message = "The alias is too short")
    private String alias;

    @JsonProperty("cardNumber")
    @NotNull(message = "The cardNumber cannot be empty")
    @Range(message = "The cardNumber must be between 13 and 16 digits",
            min = 1000000000000L, max = 9999999999999999L)
    private Long cardNumber;

    @JsonProperty("cardHolder")
    @NotBlank(message = "The cardHolder cannot be empty")
    @Size(min = 6, message = "The cardHolder's name is too short")
    @Size(max = 30, message = "The cardHolder's name must not exceed 30 characters")
    @Pattern(message = "The cardHolder must only contain letters, spaces, and dots"
        , regexp = "^[a-zA-Z\\s\\~\\´\\á\\é\\í\\ó\\ú\\ñ\\Á\\É\\Í\\Ó\\Ú\\Ñ\\.]*$")
    private String cardHolder;

    @JsonProperty("expirationDate")
    @NotBlank(message = "The expirationDate cannot be empty")
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/(\\d{4})$"
            , message = "The expirationDate must be in the format MM/yyyy")
    private String expirationDate;

    @JsonProperty("cvv")
    @NotNull(message = "The cvv cannot be empty")
    @Range(min = 100, max = 9999, message = "The cvv must be between 3 and 4 digits")
    private Integer cvv;

    @JsonProperty("bank")
    @NotBlank(message = "The bank cannot be empty")
    @Pattern(message = "The bank name must only contain letters and spaces"
            , regexp = "^[a-zA-Z\\s\\~\\´\\á\\é\\í\\ó\\ú\\ñ\\Á\\É\\Í\\Ó\\Ú\\Ñ\\.]*$")
    @Size(max = 50, message = "The bank name must not exceed 50 characters")
    @Size(min = 3, message = "Bank name is too short")
    private String bank;

    @JsonProperty("cardType")
    @NotBlank(message = "The cardType cannot be empty")
    @Size(min = 6, max = 16, message = "The cardType must be between 6 and 16 characters")
    @Pattern(message = "The cardType must only contain letters and spaces"
            , regexp = "^[a-zA-Z\\s\\~\\´\\á\\é\\í\\ó\\ú\\ñ\\Á\\É\\Í\\Ó\\Ú\\Ñ]*$")
    private String cardType;
}
