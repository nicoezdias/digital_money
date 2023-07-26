package com.digital.money.msvc.api.account.service;

import com.digital.money.msvc.api.account.handler.AlreadyRegisteredException;
import com.digital.money.msvc.api.account.handler.BadRequestException;
import com.digital.money.msvc.api.account.handler.ForbiddenException;
import com.digital.money.msvc.api.account.handler.ResourceNotFoundException;
import com.digital.money.msvc.api.account.model.Account;
import com.digital.money.msvc.api.account.model.dto.CardGetDTO;
import com.digital.money.msvc.api.account.model.dto.CardPostDTO;

import java.util.List;

public interface ICardService extends IService<CardPostDTO, CardGetDTO> {

    CardGetDTO createCard(Account account, CardPostDTO cardPostDTO) throws AlreadyRegisteredException, BadRequestException;
    List<CardGetDTO> listCards(Account account) throws ResourceNotFoundException;
    CardGetDTO findCardById(Account account, Long cardId) throws ResourceNotFoundException, ForbiddenException;
    void deleteCard(Account account, Long cardId) throws ResourceNotFoundException, ForbiddenException;

}
