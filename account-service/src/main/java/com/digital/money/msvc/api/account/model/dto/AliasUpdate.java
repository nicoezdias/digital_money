package com.digital.money.msvc.api.account.model.dto;

import com.digital.money.msvc.api.account.handler.BadRequestException;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AliasUpdate {

    @NotNull(message = "The second word cannot be null")
    @NotEmpty(message = "The second word cannot be empty")
    @Size(min = 6, max = 12, message = "The word must contain a minimum of 6 and a maximum of 12 characters")
    @JsonProperty("word_index_zero")
    private String wordIndexZero;

    @NotNull(message = "The first word cannot be null")
    @NotEmpty(message = "The first word cannot be empty")
    @Size(min = 6, max = 12, message = "The word must contain a minimum of 6 and a maximum of 12 characters")
    @JsonProperty("word_index_one")
    private String wordIndexOne;

    @NotNull(message = "The third word cannot be null")
    @NotEmpty(message = "The third word cannot be empty")
    @Size(min = 6, max = 12, message = "The word must contain a minimum of 6 and a maximum of 12 characters")
    @JsonProperty("word_index_two")
    private String wordIndexTwo;

    public String buildAlias() throws BadRequestException {

        if (wordIndexZero.length() == 0 || wordIndexOne.length() == 0 || wordIndexTwo.length() == 0) {
            throw new BadRequestException("You must choose 3 words. Words cannot be blank.");
        }

        if (wordIndexZero.equals(wordIndexOne) || wordIndexOne.equals(wordIndexTwo) || wordIndexZero.equals(wordIndexTwo)) { throw new BadRequestException("All the words must be different.");
        }

        return wordIndexZero.concat(".")
                .concat(wordIndexOne).concat(".")
                .concat(wordIndexTwo);
    }


}
