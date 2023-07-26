package com.digital.money.msvc.api.account.service.impl;

import com.digital.money.msvc.api.account.handler.*;
import com.digital.money.msvc.api.account.model.Account;
import com.digital.money.msvc.api.account.model.Card;
import com.digital.money.msvc.api.account.model.Transaction;
import com.digital.money.msvc.api.account.model.TransactionType;
import com.digital.money.msvc.api.account.model.dto.*;
import com.digital.money.msvc.api.account.model.projections.GetLastCVUs;
import com.digital.money.msvc.api.account.repository.IAccountRepository;
import com.digital.money.msvc.api.account.repository.ICardRepository;
import com.digital.money.msvc.api.account.repository.ITransactionRepository;
import com.digital.money.msvc.api.account.service.ITransactionService;
import com.digital.money.msvc.api.account.utils.mapper.AccountMapper;
import com.digital.money.msvc.api.account.utils.mapper.CardMapper;
import com.digital.money.msvc.api.account.utils.mapper.TransactionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService implements ITransactionService {

    protected TransactionMapper transactionMapper;
    protected AccountMapper accountMapper;
    protected ITransactionRepository transactionRepository;
    protected IAccountRepository accountRepository;
    protected ICardRepository cardRepository;
    protected CardMapper cardMapper;

    @Autowired
    protected TransactionService(TransactionMapper transactionMapper, AccountMapper accountMapper, ITransactionRepository transactionRepository, IAccountRepository accountRepository, ICardRepository cardRepository, CardMapper cardMapper) {
        this.transactionMapper = transactionMapper;
        this.accountMapper = accountMapper;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }


    @Transactional
    @Override
    public TransactionGetDto save(TransactionPostDto transactionPostDto, Account fromAccount, Account toAccount) {
        Transaction transactionFromUser = transactionMapper.transactionPostToTransaction(transactionPostDto);

        transactionFromUser.setFromCvu(fromAccount.getCvu());
        transactionFromUser.setToCvu(toAccount.getCvu());

        transactionFromUser.setType(TransactionType.OUTGOING);
        transactionFromUser.setAccount(fromAccount);
        transactionRepository.save(transactionFromUser);

        if(!toAccount.getAccountId().equals(-1L)) {

            Transaction transactionToUser = transactionMapper.transactionPostToTransaction(transactionPostDto);

            transactionToUser.setFromCvu(fromAccount.getCvu());
            transactionToUser.setToCvu(toAccount.getCvu());

            transactionToUser.setType(TransactionType.INCOMING);
            transactionToUser.setAccount(toAccount);
            transactionRepository.save(transactionToUser);
        }

        return transactionMapper.toTransactionGetDto(transactionFromUser);
    }

    @Transactional(readOnly = true)
    @Override
    public ListTransactionDto getLastFive(Long id, Account account){
        List<Transaction> list = transactionRepository.getLastFive(id).get();
        return new ListTransactionDto(accountMapper.toAccountGetDto(account), list);
    }

    @Override
    public Transaction findTransactionById(Long accountId, Long transactionId) throws Exception{

        Optional<Transaction> transaction = transactionRepository.findByAccount_AccountIdAndTransactionId(accountId,transactionId);

        if (transaction.isEmpty()) {
            throw new ResourceNotFoundException("Not transference found for that id");
        }

        return transaction.get();
    }

    @Override
    public ListTransactionDto findAllSorted(Long id, Account account) {
        List<Transaction> list = transactionRepository.findAllSorted(id).get();
        return new ListTransactionDto(accountMapper.toAccountGetDto(account), list);
    }

    //* ///////// UTILS ///////// *//
    @Override
    public Transaction checkId(Long id) throws ResourceNotFoundException {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new ResourceNotFoundException(msjIdError + " id: " + id);
        }
        return transaction.get();
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

    @Transactional
    @Override
    public CardTransactionGetDTO processCardTransaction(Long id, CardTransactionPostDTO cardTransactionPostDTO) throws ResourceNotFoundException, ForbiddenException, PaymentRequiredException, BadRequestException {

        Card card = cardRepository.findByCardId(cardTransactionPostDTO.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("The card doesn't exist"));

        CardGetDTO cardGetDTO = cardMapper.toCardGetDTO(card);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The account doesn't exist"));

        if (!card.getAccount().getAccountId().equals(id)) {
            throw new ForbiddenException("The card doesn't belong to the account");
        }

       if (!isExpirationDateValid(card.getExpirationDate())) {
           throw new BadRequestException("The card you are trying to use is expired. ");
       }

        if (cardTransactionPostDTO.getAmount() == 0.0) {
            throw new BadRequestException("The amount can't be 0. Please enter a valid amount");
        } else if (cardTransactionPostDTO.getAmount() < 0.0) {
            throw new BadRequestException("The amount can't be negative. Please enter a valid amount");
        }

        Transaction transaction = transactionMapper.cTPDTOToTransaction(cardTransactionPostDTO);
        transaction.setAmount(cardTransactionPostDTO.getAmount());
        transaction.setRealizationDate(LocalDateTime.now());
        transaction.setDescription("You deposited $" + cardTransactionPostDTO.getAmount() + " from " +
                cardGetDTO.getBank() + " " + card.getCardType());
        transaction.setFromCvu(String.valueOf(card.getCardNumber()));
        transaction.setToCvu(account.getCvu());
        transaction.setType(TransactionType.INCOMING);
        transaction.setAccount(account);

        //card.setCardBalance(card.getCardBalance() - cardTransactionPostDTO.getAmount());

        account.setAvailableBalance(account.getAvailableBalance() + cardTransactionPostDTO.getAmount());

        cardRepository.save(card);

        accountRepository.save(account);

        transactionRepository.save(transaction);

        CardTransactionGetDTO cTGDTO = transactionMapper.transactionToCardTransactionGetDTO(transaction);
        cTGDTO.setCardNumber(cardGetDTO.getCardNumber());

        return cTGDTO;
    }

    @Override
    public List<Transaction> getAllTransactionsByAmountRange(Integer rangoSelected, Long accountId) throws Exception {
        Double[] rangos = {0.0,0.0,1000.0,5000.0,20000.0,100000.0};
        List<Transaction> transactions = new ArrayList<>();

        if(rangoSelected<=0 || rangoSelected>5){
            throw new SelectOutOfBoundException("Please select a option within the range");
        }

        Double firstR = rangos[rangoSelected];

        if(rangoSelected==5){
            transactions = transactionRepository.findByAmountGreaterThanEqualAndAccount_AccountId(firstR,accountId);
        }else{
            Double secondR = rangos[rangoSelected+1];
            transactions = transactionRepository.findByAmountBetweenAndAccount_AccountId(firstR,secondR,accountId);
        }

        return transactions;
    }

    @Value("${spring.datasource.url}")
    private String urlDB;

    @Value("${spring.datasource.username}")
    private String userDB;

    @Value("${spring.datasource.password}")
    private String passDB;

    @Value("${spring.datasource.driver-class-name}")
    private String connectorDB;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public ResultSet getTransactionsFromDB(Long accountId, String startDate, String endDate, Integer rangeSelect, String type) throws Exception{

        String query = "SELECT * FROM transactions WHERE account_id = ?";
        ArrayList<Object> params = new ArrayList<Object>();
        LocalDateTime startDateLDT, endDateLDT;

        params.add(accountId);

        if (startDate!=null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

            try {
                startDateLDT = LocalDate.parse(startDate, dateFormatter).atStartOfDay();

                if(endDate!=null){
                    try {
                        endDateLDT = LocalDate.parse(endDate, dateFormatter).atStartOfDay();
                    }
                    catch (DateTimeException e) {
                        String msg = e.getMessage();
                        throw new BadRequestException(msg);
                    }
                }else {
                    endDateLDT = LocalDateTime.now();
                }

            } catch (DateTimeException e) {
                String msg = e.getMessage();
                throw new BadRequestException(msg);
            }

            if (endDateLDT.isBefore(startDateLDT)) {
                throw new BadRequestException("The start date must be before the end date");
            }

            params.add(startDateLDT);
            params.add(endDateLDT);

            query+=" AND realization_date BETWEEN ? AND ?";
        }else{
            if(endDate!=null){
                throw new BadRequestException("Only an end date was entered. You must enter a start date.");
            }
        }

        TransactionType transactionType = null;

        if(type!=null) {
            if (type.equals("INCOMING")) {
                transactionType = TransactionType.INCOMING;
            } else if (type.equals("OUTGOING")) {
                transactionType = TransactionType.OUTGOING;
            }else{
                throw new BadRequestException("Incorrect transaction type. Please choose INCOMING or OUTGOING");
            }
        }

        if (transactionType!=null){
            query+=" AND type = ?";
            params.add(transactionType.toString());
        }

        Double[] rangos = {0.0,0.0,1000.0,5000.0,20000.0,100000.0};

        if(rangeSelect!=null) {
            if (rangeSelect >= 1 && rangeSelect <= 5) {
                Double firstR = rangos[rangeSelect];
                params.add(firstR);

                if (rangeSelect == 5) {
                    query += " AND amount >= ?";
                } else {
                    Double secondR = rangos[rangeSelect + 1];
                    query += " AND amount BETWEEN ? AND ? ";
                    params.add(secondR);
                }

            }
            else {
                throw new SelectOutOfBoundException("Please select a option within the range");
            }
        }

        Object[] parameters = params.toArray();

        Class.forName(connectorDB);
        Connection connection= DriverManager.getConnection(urlDB,userDB,passDB);

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        for(int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i+1, parameters[i]);
        }

        return preparedStatement.executeQuery();

    }

    @Override
    public List<Transaction> getTransactionsFromResultSet(ResultSet resultSet, Account account) throws Exception{

        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        List<Transaction> transactionList = new ArrayList<>();

        while (resultSet.next()) {

            Transaction transaction = new Transaction();
            JSONObject jsonObject = new JSONObject();

            for (int i = 1; i <= columnsNumber; i++) {

                String columnName = rsmd.getColumnName(i);
                String columnValue = resultSet.getString(i);

                jsonObject.put(columnName,columnValue);
            }

            transaction = transactionMapper.jsonToTransaction(jsonObject);
            transaction.setAccount(account);
            transactionList.add(transaction);
        }

        return transactionList;
    }

    @Override
    public List<TransactionGetDto> getLastTenTransactions(Long id) throws Exception{
        List<Transaction> transactions = transactionRepository.findByAccount_AccountIdAndTypeOrderByRealizationDateDesc(id,TransactionType.OUTGOING,PageRequest.of(0,10));

        return transactions.stream().map(transactionMapper::toTransactionGetDto).collect(Collectors.toList());

    }

    @Override
    public List<GetLastCVUs> getLastFiveReceivers(Long id) throws Exception {
        return transactionRepository.findLastFiveReceivers(id, PageRequest.of(0,5));
    }

    @Override
    public TransactionGetDto findTransactionDTO(Long id, Long transferenceID) throws Exception{
        Transaction transaction = findTransactionById(id,transferenceID);
        return transactionMapper.toTransactionGetDto(transaction);
    }

}
