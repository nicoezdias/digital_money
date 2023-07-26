package com.digital.money.msvc.api.account.utils.mapper;


import com.digital.money.msvc.api.account.model.Account;
import com.digital.money.msvc.api.account.model.dto.AccountGetDto;
import com.digital.money.msvc.api.account.model.dto.AccountPostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    public abstract Account toAccount(AccountPostDto accountPostDto);

    public abstract AccountGetDto toAccountGetDto(Account account);

//    @Mappings({
//            @Mapping(target = "email", expression="java(clientPostUpdateDto.getEmail().toLowerCase())"),
//            @Mapping(target = "deleted", source="deleted"),
//            @Mapping(target = "state", source="state"),
//    })
//    public abstract Account toClientUpdate(ClientPostDto clientPostUpdateDto, ClientState state, boolean deleted);

}
