package com.digital.money.msvc.api.account.utils.mapper;

import com.digital.money.msvc.api.account.model.Transaction;
import com.digital.money.msvc.api.account.model.TransactionType;
import com.digital.money.msvc.api.account.model.dto.CardTransactionGetDTO;
import com.digital.money.msvc.api.account.model.dto.CardTransactionPostDTO;
import com.digital.money.msvc.api.account.model.dto.TransactionGetDto;
import com.digital.money.msvc.api.account.model.dto.TransactionPostDto;
import org.json.JSONObject;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    public abstract Transaction toTransaction(TransactionPostDto transactionPostDto);

    public Transaction transactionPostToTransaction(TransactionPostDto transactionPostDto){
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionPostDto.getAmount());
        transaction.setDescription(transactionPostDto.getDescription());
        transaction.setFromCvu(transactionPostDto.getFromAccount());
        transaction.setToCvu(transactionPostDto.getToAccount());
        transaction.setRealizationDate(LocalDateTime.now());
        return  transaction;

    }

    public abstract TransactionGetDto toTransactionGetDto(Transaction transaction);

    public abstract Transaction cTPDTOToTransaction(CardTransactionPostDTO cardTransactionPostDTO);

    public abstract CardTransactionGetDTO transactionToCardTransactionGetDTO(Transaction transaction);

    public Transaction jsonToTransaction(JSONObject jsonObject) throws Exception {

        String stringDate = jsonObject.getString("realization_date");
        Integer end = stringDate.indexOf(" ");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime realizationDate = LocalDate.parse(stringDate.substring(0,end), dateFormatter).atStartOfDay();
        String type = jsonObject.getString("type");

        TransactionType transactionType = null;

        if(type!=null) {
            if (type.equals("INCOMING")) {
                transactionType = TransactionType.INCOMING;
            } else if (type.equals("OUTGOING")) {
                transactionType = TransactionType.OUTGOING;
            }
        }


        Transaction transaction = new Transaction(
                jsonObject.getLong("transaction_id"),
                jsonObject.getDouble("amount"),
                realizationDate,
                jsonObject.getString("description"),
                jsonObject.getString("from_cvu"),
                jsonObject.getString("to_cvu"),
                transactionType,
               null
        );

        return transaction;


    };

//    @Mappings({
//            @Mapping(target = "email", expression="java(clientPostUpdateDto.getEmail().toLowerCase())"),
//            @Mapping(target = "deleted", source="deleted"),
//            @Mapping(target = "state", source="state"),
//    })
//    public abstract Account toClientUpdate(ClientPostDto clientPostUpdateDto, ClientState state, boolean deleted);

}
