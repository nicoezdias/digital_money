package com.digital.money.msvc.api.account.controller;

import com.digital.money.msvc.api.account.handler.*;
import com.digital.money.msvc.api.account.model.Transaction;
import com.digital.money.msvc.api.account.model.dto.*;
import com.digital.money.msvc.api.account.service.impl.AccountService;
import com.digital.money.msvc.api.account.utils.GeneratorPdf;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    //* ///////// ACCOUNT ///////// *//
    @Operation(summary = "Find an account by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        return ResponseEntity.ok(accountService.findById(id, token));
    }

    @Operation(summary = "Save an account", hidden = true)
    @PostMapping
    public ResponseEntity<Object> save(@RequestParam(name = "user_id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(userId));
    }

    @Operation(summary = "Update account alias")
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateAlias(@PathVariable(name = "id") Long id,
                                              @RequestBody AliasUpdate aliasUpdate,
                                              @RequestHeader("Authorization") String token) throws AlreadyRegisteredException, ResourceNotFoundException, BadRequestException, ForbiddenException, JSONException {
        String response = accountService.updateAlias(id, aliasUpdate, token);
        return ResponseEntity.ok(response);
    }

    //* ///////// TRANSACTIONS ///////// *//
    @Operation(summary = "Find last five transactions by account id")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<Object> findAllByAccountId(@PathVariable(name = "id") Long account_id,
                                                     @RequestHeader("Authorization") String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        ListTransactionDto listTransactionDto = accountService.findLastFiveTransactions(account_id, token);
        if (listTransactionDto.getTransactions().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("The account doesn't have any transactions");
        }
        return ResponseEntity.ok(listTransactionDto);
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<Object> findAllActivity(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) throws ForbiddenException, JSONException, ResourceNotFoundException {
        ListTransactionDto listTransactionDto = accountService.findAllTransactions(id, token);
        if (listTransactionDto.getTransactions().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("The account doesn't have any transactions");
        }
        return ResponseEntity.ok(listTransactionDto);
    }

    @GetMapping("/{id}/activity/{transferenceID}")
    public ResponseEntity<Transaction> oneActivity(@PathVariable("id") Long id,
                                                   @PathVariable("transferenceID") Long transferenceID,
                                                   @RequestHeader("Authorization") String token) throws Exception {
        return ResponseEntity.ok(accountService.findTransactionById(id, transferenceID, token));
    }

    //* ///////// CARDS ///////// *//
    @Operation(summary = "Add a card to an account")
    @PostMapping(value = "/{id}/cards", consumes = "application/json")
    public ResponseEntity<?> addCard(@PathVariable(name = "id") Long id,
                                     @Valid @RequestBody CardPostDTO cardPostDTO,
                                     @RequestHeader("Authorization") String token) throws ResourceNotFoundException, AlreadyRegisteredException, BadRequestException, ForbiddenException, JSONException {
        return ResponseEntity.ok(accountService.addCard(id, cardPostDTO, token));
    }

    @Operation(summary = "List all cards from an account")
    @GetMapping(value = "/{id}/cards", produces = "application/json")
    public ResponseEntity<?> listCards(@PathVariable(name = "id") Long id,
                                       @RequestHeader("Authorization") String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        List<CardGetDTO> list = accountService.listAllCards(id, token);
        if (list.isEmpty()) {
            return new ResponseEntity("The are no cards associated with this account"
                    , HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @Operation(summary = "Find a card by id")
    @GetMapping(value = "/{id}/cards/{cardId}", produces = "application/json")
    public ResponseEntity<?> findCardById(@PathVariable(name = "id") Long id,
                                          @PathVariable(name = "cardId") Long cardId,
                                          @RequestHeader("Authorization") String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        return ResponseEntity.ok(accountService.findCardFromAccount(id, cardId, token));
    }

    @Operation(summary = "Delete a card from an account")
    @DeleteMapping(value = "/{id}/cards/{cardId}", produces = "application/json")
    public ResponseEntity<?> deleteCard(@PathVariable(name = "id") Long id,
                                        @PathVariable(name = "cardId") Long cardId,
                                        @RequestHeader("Authorization") String token) throws ResourceNotFoundException, ForbiddenException, JSONException {
        accountService.removeCardFromAccount(id, cardId, token);
        return ResponseEntity.ok("Card successfully removed from account");
    }

    @Operation(summary = "Deposit money into account from card")
    @PostMapping(value = "/{id}/deposit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> deposit(@PathVariable("id") Long id,
                                          @RequestHeader("Authorization") String token,
                                          @Valid @RequestBody CardTransactionPostDTO cardTransactionPostDTO) throws PaymentRequiredException, ForbiddenException, ResourceNotFoundException, BadRequestException, JSONException {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.depositMoney(id, cardTransactionPostDTO, token));
    }

    @GetMapping("/{id}/activity/amount/{amountRange}")
    public ResponseEntity<?> getTransactionsByAmountRange(@PathVariable("id") Long id, @PathVariable("amountRange") Integer range, @RequestHeader("Authorization") String token) throws Exception{
        return accountService.getTransactionsByAmountRange(id, range, token);

    }

    @GetMapping("/{id}/activity/filters")
    public ResponseEntity<?> getTransactionsWithFilters(@PathVariable("id") Long id, @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value ="endDate", required = false) String endDate, @RequestParam(value ="amountRange", required = false) Integer amount, @RequestParam(value ="type", required = false) String type, @RequestHeader("Authorization") String token) throws Exception{
        return accountService.getTransactionsWithFilters(id, startDate, endDate, amount, type, token);

    }

    @GetMapping("/{id}/transferences")
    public ResponseEntity<List <TransactionGetDto>> getLastTransactions(@PathVariable("id") Long id,@RequestHeader("Authorization") String token)throws Exception{
        return accountService.getLastTenTransactions(id, token);
    }


    @PostMapping(value = "/{id}/transferences", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TransactionGetDto> transferMoney(@PathVariable("id") Long id,
                                                           @RequestHeader("Authorization") String token,
                                                           @Valid @RequestBody TransactionPostDto transactionPostDto) throws Exception {

        TransactionGetDto transactionSuccessful = accountService.transferMoney(id, token, transactionPostDto);
        return new ResponseEntity<>(transactionSuccessful, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}/transferences/{transferenceID}/comprobante")
    public ResponseEntity<?> viewPdf(@PathVariable("id") Long id,
                                     @PathVariable("transferenceID") Long transferenceID,
                                     @RequestHeader("Authorization") String token,
                                     HttpServletResponse response) throws Exception {

        //Headers..
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=transfer_".concat(currentDateTime).concat(".pdf");
        response.setHeader(headerkey, headervalue);

        TransactionGetDto transaction = accountService.getTransactionDto(id, transferenceID,token);

        GeneratorPdf generator = new GeneratorPdf();
        generator.setTransactionSuccessful(transaction);
        generator.generate(response);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/transferences/lastReceivers")
    public ResponseEntity<List <Map<String,String>>> getLastFiveAccountsTransferred(@PathVariable("id") Long id,
                                                                                    @RequestHeader("Authorization") String token) throws Exception {
        return accountService.getLastFiveAccountsTransferred(id, token);
    }
}
