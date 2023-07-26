package com.digital.money.msvc.api.account.utils.mapper;

import com.digital.money.msvc.api.account.model.Card;
import com.digital.money.msvc.api.account.model.dto.CardGetDTO;
import com.digital.money.msvc.api.account.model.dto.CardPostDTO;
import org.mapstruct.Mapper;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public abstract class CardMapper {

    public abstract Card toCard(CardPostDTO cardPostDTO);

    public CardGetDTO toCardGetDTO(Card card) {
        CardGetDTO cardGetDTO = new CardGetDTO();
        cardGetDTO.setCardId(card.getCardId());
        cardGetDTO.setAlias(card.getAlias());
        cardGetDTO.setCardNumber(hideCardNumber(card.getCardNumber()));
        cardGetDTO.setCardHolder(card.getCardHolder());
        cardGetDTO.setBank(card.getBank());
        cardGetDTO.setCardNetwork(card.getCardNetwork());
        cardGetDTO.setCardType(card.getCardType());
        return cardGetDTO;
    }

    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

    public String hideCardNumber(Long cardNumber) {
        String cardNumberString = cardNumber.toString();
        String lastFourDigits = cardNumberString.substring(cardNumberString.length() - 4);
        return "**** " + lastFourDigits;
    }
}
