package com.digital.money.msvc.api.account.service.impl;

import com.digital.money.msvc.api.account.handler.*;
import com.digital.money.msvc.api.account.model.Account;
import com.digital.money.msvc.api.account.model.Transaction;
import com.digital.money.msvc.api.account.model.dto.*;
import com.digital.money.msvc.api.account.model.projections.GetLastCVUs;
import com.digital.money.msvc.api.account.repository.IAccountRepository;
import com.digital.money.msvc.api.account.service.IAccountService;
import com.digital.money.msvc.api.account.utils.GeneratorKeys;
import com.digital.money.msvc.api.account.utils.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
@Service
public class AccountService implements IAccountService {
    private final AccountMapper accountMapper;
    private final TransactionService transactionService;
    private final IAccountRepository accountRepository;
    private final CardServices cardService;

    @Autowired
    public AccountService(AccountMapper accountMapper,
                          TransactionService transactionService,
                          IAccountRepository accountRepository,
                          CardServices cardService) {
        this.accountMapper = accountMapper;
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
        this.cardService = cardService;
    }

    //* ///////// ACCOUNT ///////// *//
    @Override
    public AccountGetDto findById(Long id, String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);
        return accountMapper.toAccountGetDto(account);
    }

    @Transactional
    @Override
    public AccountGetDto save(Long userId) {
        Account account = new Account();
        account.setUserId(userId);

        String cvu = GeneratorKeys.generateCvu();
        while (accountRepository.findByCvu(cvu).isPresent()) {
            cvu = GeneratorKeys.generateCvu();
        }
        account.setCvu(cvu);

        String alias = GeneratorKeys.generateAlias();
        while (accountRepository.findByCvu(alias).isPresent()) {
            alias = GeneratorKeys.generateCvu();
        }
        account.setAlias(alias);
        account.setAvailableBalance(0.0);

        Account accountResponse = accountRepository.save(account);
        return accountMapper.toAccountGetDto(accountResponse);
    }

    @Transactional
    @Override
    public String updateAlias(Long id, AliasUpdate aliasUpdate, String token) throws AlreadyRegisteredException, ResourceNotFoundException, BadRequestException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);
        String newAlias = aliasUpdate.buildAlias().toLowerCase();

        if (newAlias.equals(account.getAlias())) {
            throw new AlreadyRegisteredException("The alias is already registered");
        }

        Optional<Account> duplicateAlias = accountRepository.findByAlias(newAlias);
        if (duplicateAlias.isEmpty()) {
            account.setAlias(newAlias);
            accountRepository.save(account);
            return String.format("New Alias: %s", account.getAlias());
        } else {
            throw new AlreadyRegisteredException("The alias is already registered");
        }
    }

    //* ///////// TRANSACTIONS ///////// *//
    @Transactional(readOnly = true)
    @Override
    public ListTransactionDto findLastFiveTransactions(Long id, String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);
        return transactionService.getLastFive(id, account);
    }

    @Override
    public ListTransactionDto findAllTransactions(Long id, String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);

        return transactionService.findAllSorted(id, account);
    }

    @Override
    public Transaction findTransactionById(Long accountId, Long transactionId, String token) throws Exception{
        Account account = checkId(accountId);
        validateAccountBelongsUser(account, token);

        return transactionService.findTransactionById(accountId, transactionId);
    }

    //* ///////// CARDS ///////// *//
    @Transactional
    @Override
    public CardGetDTO addCard(Long id, CardPostDTO cardPostDTO, String token) throws ResourceNotFoundException, AlreadyRegisteredException, BadRequestException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);

        return cardService.createCard(account, cardPostDTO);
    }

    @Override
    public List<CardGetDTO> listAllCards(Long id, String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);

        return cardService.listCards(account);
    }

    @Override
    public CardGetDTO findCardFromAccount(Long id, Long cardId, String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);
        return cardService.findCardById(account, cardId);
    }

    @Override
    public void removeCardFromAccount(Long id, Long cardId, String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);

        cardService.deleteCard(account, cardId);
    }

    //* ///////// UTILS ///////// *//
    @Override
    public Account checkId(Long id) throws ResourceNotFoundException {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new ResourceNotFoundException(msjIdError + " id: " + id);
        }
        return account.get();
    }

    private String decodeToken(String token) throws JSONException {
        String[] jwtParts = token.split("\\.");
        JSONObject payload = new JSONObject(new String(Base64.getUrlDecoder().decode(jwtParts[1])));
        return payload.getString("user_id");
    }

    private void validateAccountBelongsUser(Account account, String token) throws JSONException, ForbiddenException {
        String userId = decodeToken(token);
        Long userIdL = Long.valueOf(userId);
        if (!account.getUserId().equals(userIdL)) {
            throw new ForbiddenException("You don't have access to that account");
        }
    }

    @Transactional
    @Override
    public CardTransactionGetDTO depositMoney(Long id, CardTransactionPostDTO cardTransactionPostDTO, String token) throws ResourceNotFoundException, PaymentRequiredException, ForbiddenException, BadRequestException, JSONException {
        Account account = checkId(id);
        validateAccountBelongsUser(account, token);
        return transactionService.processCardTransaction(id, cardTransactionPostDTO);
    }

    @Override
    public ResponseEntity<?> getTransactionsByAmountRange(Long accountId, Integer rangoSelected, String token) throws Exception {

        Account account = checkId(accountId);
        validateAccountBelongsUser(account, token);

        ListTransactionDto listTransactionDto = new ListTransactionDto();

        List<Transaction> transactions = transactionService.getAllTransactionsByAmountRange(rangoSelected, accountId);

        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Lo sentimos. No se encontró contenido para este rango");
        }

        listTransactionDto.setTransactions(transactions);
        listTransactionDto.setAccount(findById(accountId, token));

        return ResponseEntity.status(HttpStatus.OK).body(listTransactionDto);
    }

    @Override
    public ResponseEntity<ListTransactionDto> getTransactionsWithFilters(Long accountId, String startDate, String endDate, Integer rangeSelect, String type, String token) throws Exception {
        Account account = checkId(accountId);
        validateAccountBelongsUser(account, token);

        ResultSet resultSet = transactionService.getTransactionsFromDB(accountId, startDate, endDate, rangeSelect, type);

        List<Transaction> transactions = transactionService.getTransactionsFromResultSet(resultSet, account);

        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        accountRepository.findAll();


        ListTransactionDto listTransactionDto = new ListTransactionDto();

        listTransactionDto.setTransactions(transactions);
        listTransactionDto.setAccount(accountMapper.toAccountGetDto(account));

        return ResponseEntity.status(HttpStatus.OK).body(listTransactionDto);
    }

    @Override
    public ResponseEntity<List<Map<String, String>>> getLastFiveAccountsTransferred(Long id, String token) throws Exception {

        findById(id, token);
        List<GetLastCVUs> getLastCVUsList = transactionService.getLastFiveReceivers(id);

        if (getLastCVUsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        List<Map<String, String>> cvus = new ArrayList<>();

        for (GetLastCVUs getLastCVUs : getLastCVUsList) {
            Map mapper = new HashMap<>();
            mapper.put("cvu", getLastCVUs.getTo_Cvu());
            mapper.put("last_date:",getLastCVUs.getRealization_date().toString());

            cvus.add(mapper);
        }

        return ResponseEntity.status(HttpStatus.OK).body(cvus);
    }

    @Override
    public ResponseEntity<List<TransactionGetDto>> getLastTenTransactions(Long id, String token)throws Exception{
        findById(id,token);

        List<TransactionGetDto> transactionGetDtos = transactionService.getLastTenTransactions(id);

        if(transactionGetDtos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok(transactionGetDtos);

    }

    @Override
    public TransactionGetDto transferMoney(Long id, String token, TransactionPostDto transactionPostDto) throws Exception {
        AccountGetDto accountGetDto = findById(id, token);

        if (transactionPostDto.getAmount() < 1) {
            throw new BadRequestException("Amount cannot be less than 1");
        }

        Optional<Account> fromAccount;

        Boolean numericError = Boolean.FALSE;
        try {
            new BigInteger(transactionPostDto.getFromAccount());

            if (transactionPostDto.getFromAccount().length() != 22) {
                numericError = Boolean.TRUE;
                throw new Exception();
            }

            fromAccount = accountRepository.findByCvu(transactionPostDto.getFromAccount());

        } catch (Exception e) {

            if (numericError) {
                throw new BadRequestException("The account from which you want to send that you have entered does not comply with CVU/CBU rules. Please enter a 22 digit number");
            }

            int posPunto1 = 0, posPunto2 = 0;

            posPunto1 = transactionPostDto.getFromAccount().indexOf(".");
            posPunto2 = transactionPostDto.getFromAccount().indexOf(".", posPunto1 + 1);

            int aux = transactionPostDto.getFromAccount().length() - 1;

            if (posPunto2 == -1 || posPunto1 == -1 || posPunto1 == 0 || transactionPostDto.getFromAccount().charAt(aux) == '.') {
                throw new BadRequestException("The account from which you want to send that you have entered does not comply with the alias rules");
            } else {
                fromAccount = accountRepository.findByAlias(transactionPostDto.getFromAccount());
            }

        }

        if (!accountGetDto.getAccountId().equals(fromAccount.get().getAccountId()) || fromAccount.isEmpty()) {
            throw new ForbiddenException("You do not have any associated account from which you are sending");
        }

        if (fromAccount.get().getAlias().equals(transactionPostDto.getToAccount()) || fromAccount.get().getCvu().equals(transactionPostDto.getToAccount())) {
            throw new BadRequestException("You can't transfer money to the same account");
        }

        if (transactionPostDto.getAmount() > fromAccount.get().getAvailableBalance()) {
            throw new AmountOfMoneyException("Account balance less than the chosen amount");
        }

        Optional<Account> toAccount = accountRepository.findByCvu(transactionPostDto.getToAccount());
        Account accountAux = new Account();

        if (toAccount.isEmpty()) {
            toAccount = accountRepository.findByAlias(transactionPostDto.getToAccount());

            if (toAccount.isEmpty()) {
                accountAux.setAccountId(-1L);

                numericError = Boolean.FALSE;

                try {
                    new BigInteger(transactionPostDto.getToAccount());

                    if (transactionPostDto.getToAccount().length() != 22) {
                        numericError = Boolean.TRUE;
                        throw new Exception();
                    }

                    accountAux.setCvu(transactionPostDto.getToAccount());
                } catch (Exception e) {

                    if (numericError) {
                        throw new BadRequestException("The account you are trying to send to does not meet the CVU/CBU rules. Please enter a 22 digit number");
                    }

                    Long hash = 0L;
                    for (char c : transactionPostDto.getToAccount().toCharArray()) {
                        hash = 31L * hash + c;
                    }

                    Random random = new Random(hash);
                    String cvu = "";
                    for (int f = 1; f <= 22; f++) {
                        cvu += String.valueOf(random.nextInt(9));
                    }

                    accountAux.setCvu(cvu);
                }


            }
        }

        fromAccount.get().setAvailableBalance(fromAccount.get().getAvailableBalance() - transactionPostDto.getAmount());
        accountRepository.save(fromAccount.get());

        if (toAccount.isPresent()) {
            toAccount.get().setAvailableBalance(toAccount.get().getAvailableBalance() + transactionPostDto.getAmount());
            accountAux = toAccount.get();
            accountRepository.save(toAccount.get());
        }

        return transactionService.save(transactionPostDto, fromAccount.get(), accountAux);
    }

    @Override
    public TransactionGetDto getTransactionDto(Long accountID, Long transferenceID, String token) throws Exception{
        AccountGetDto accountGetDto = findById(accountID,token);

        TransactionGetDto transactionGetDto = transactionService.findTransactionDTO(accountID,transferenceID);

        if (!transactionGetDto.getAccount().getAccountId().equals(accountID)) {
            throw new ForbiddenException("You don't have any account with that id");
        }

        return transactionService.findTransactionDTO(accountID,transferenceID);

    }

}
