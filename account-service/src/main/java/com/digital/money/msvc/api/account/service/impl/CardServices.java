package com.digital.money.msvc.api.account.service.impl;

import com.digital.money.msvc.api.account.handler.AlreadyRegisteredException;
import com.digital.money.msvc.api.account.handler.BadRequestException;
import com.digital.money.msvc.api.account.handler.ForbiddenException;
import com.digital.money.msvc.api.account.handler.ResourceNotFoundException;
import com.digital.money.msvc.api.account.model.Account;
import com.digital.money.msvc.api.account.model.Card;
import com.digital.money.msvc.api.account.model.dto.CardGetDTO;
import com.digital.money.msvc.api.account.model.dto.CardPostDTO;
import com.digital.money.msvc.api.account.repository.ICardRepository;
import com.digital.money.msvc.api.account.service.ICardService;
import com.digital.money.msvc.api.account.utils.mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardServices implements ICardService {

    private final ICardRepository cardRepository;
    private final CardMapper cardMapper;

    @Autowired
    public CardServices(ICardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    @Override
    public CardGetDTO createCard(Account account, CardPostDTO cardPostDTO) throws AlreadyRegisteredException, BadRequestException {
        if (checkIfCardExists(cardPostDTO.getCardNumber())) {
            throw new AlreadyRegisteredException("The card you are trying to create already exists");
        }
        if (!isExpirationDateValid(cardPostDTO.getExpirationDate())) {
            throw new BadRequestException("The card you are trying to add is expired. " +
                    "Please make sure the expiration date is in the future.");
        }
        if (!isCardNumberValid(cardPostDTO.getCardNumber())) {
            throw new BadRequestException("The card you are trying to add is invalid. " +
                    "Please make sure the card number is valid.");
        }

        Card card = cardMapper.toCard(cardPostDTO);
        card.setCardNetwork(guessTheCardNetwork(cardPostDTO.getCardNumber()));

        if (card.getCardNetwork().equals("AMEX")) {
            if (cardPostDTO.getCvv().toString().length() != 4) {
                throw new BadRequestException("Please make sure the cvv is valid");
            }
        } else {
            if (cardPostDTO.getCvv().toString().length() != 3) {
                throw new BadRequestException("Please make sure the cvv is valid");
            }
        }
        card.setAccount(account);
        cardRepository.save(card);

        return cardMapper.toCardGetDTO(card);
    }

    @Override
    public List<CardGetDTO> listCards(Account account) {
        List<Card> cards = cardRepository.findAllByAccountAccountId(account.getAccountId());
        return cards.stream().map(cardMapper::toCardGetDTO).collect(Collectors.toList());
    }

    @Override
    public CardGetDTO findCardById(Account account, Long cardId) throws ResourceNotFoundException, ForbiddenException {
        Optional<Card> entityResponse = cardRepository.findByCardId(cardId);

        if (entityResponse.isPresent()) {
            Card card = entityResponse.get();
            if (!card.getAccount().getAccountId().equals(account.getAccountId())) {
                throw new ForbiddenException("The card you are trying to select belongs to another " +
                        "account.");
            }
        }

        if (entityResponse.isPresent()) {
            Card card = entityResponse.get();
            return cardMapper.toCardGetDTO(card);
        } else {
            throw new ResourceNotFoundException("The card you are trying to find does not exist");
        }
    }

    @Override
    public void deleteCard(Account account, Long cardId) throws ResourceNotFoundException, ForbiddenException {

        Optional<Card> entityResponse = cardRepository.findByCardId(cardId);

        if (entityResponse.isPresent()) {
            Card card = entityResponse.get();
            if (!card.getAccount().getAccountId().equals(account.getAccountId())) {
                throw new ForbiddenException("The card you are trying to delete belongs to another " +
                        "account.");
            }
        }

        if (entityResponse.isPresent()) {
            Card card = entityResponse.get();
            cardRepository.delete(card);
        } else {
            throw new ResourceNotFoundException("The card you are trying to delete does not exist");
        }
    }

    @Override
    public CardGetDTO save(CardPostDTO cardPostDTO) {
        return null;
    }

    //* ///////// UTILS ///////// *//
    private boolean checkIfCardExists(Long cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).isPresent();
    }

    private boolean isExpirationDateValid(String expirationDate) {
        try {
            YearMonth yearMonth = YearMonth.parse(expirationDate, cardMapper.formatter);
            YearMonth currentYearMonth = YearMonth.now();
            return yearMonth.isAfter(currentYearMonth) || yearMonth.equals(currentYearMonth);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isCardNumberValid(Long cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.toString().length() - 1; i >= 0; i--) {
            long n = Long.parseLong(cardNumber.toString().substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {n = (n % 10) + 1;}
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private String guessTheCardNetwork(Long cardNumber){
        String cardNumberString = cardNumber.toString();
        String prefix = cardNumberString.substring(0, 2);

        return switch (prefix) {
            case "40", "41", "42", "43", "44", "45", "46", "47", "48", "49" -> "Visa";
            case "30", "36", "38" -> "Diners Club International";
            case "34", "37" -> "AMEX";
            case "51", "52", "53", "54", "55" -> "MasterCard";
            case "60", "61", "62", "63", "64", "65" -> "Discover";
            default -> "Unknown";
        };
    }

}
