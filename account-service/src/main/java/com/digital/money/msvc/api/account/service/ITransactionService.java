package com.digital.money.msvc.api.account.service;

import com.digital.money.msvc.api.account.handler.ResourceNotFoundException;
import com.digital.money.msvc.api.account.model.Account;
import com.digital.money.msvc.api.account.handler.BadRequestException;
import com.digital.money.msvc.api.account.handler.PaymentRequiredException;
import com.digital.money.msvc.api.account.handler.ForbiddenException;
import com.digital.money.msvc.api.account.model.Transaction;
import com.digital.money.msvc.api.account.model.dto.ListTransactionDto;
import com.digital.money.msvc.api.account.model.dto.CardTransactionGetDTO;
import com.digital.money.msvc.api.account.model.dto.CardTransactionPostDTO;
import com.digital.money.msvc.api.account.model.dto.TransactionGetDto;
import com.digital.money.msvc.api.account.model.dto.TransactionPostDto;
import com.digital.money.msvc.api.account.model.projections.GetLastCVUs;

import java.sql.ResultSet;
import java.util.List;

public interface ITransactionService extends ICheckId<Transaction> {
    ListTransactionDto getLastFive(Long id, Account account);

    TransactionGetDto save(TransactionPostDto transactionPostDto,Account fromAccount, Account toAccount);
    Transaction findTransactionById(Long accountId, Long transactionId) throws Exception;
    ListTransactionDto findAllSorted(Long id, Account account);
    CardTransactionGetDTO processCardTransaction(Long accountId, CardTransactionPostDTO cardTransactionPostDTO) throws ResourceNotFoundException, ForbiddenException, PaymentRequiredException, BadRequestException;

    List<Transaction> getAllTransactionsByAmountRange(Integer rangoSelected, Long accountId) throws Exception;
    ResultSet getTransactionsFromDB(Long accountId, String startDate, String endDate, Integer rangeSelect, String type) throws Exception;

    List<Transaction> getTransactionsFromResultSet(ResultSet resultSet, Account account) throws Exception;

    List<TransactionGetDto> getLastTenTransactions(Long id) throws Exception;

    List<GetLastCVUs> getLastFiveReceivers(Long id) throws Exception;

    TransactionGetDto findTransactionDTO(Long id, Long transferenceID) throws Exception;
}
