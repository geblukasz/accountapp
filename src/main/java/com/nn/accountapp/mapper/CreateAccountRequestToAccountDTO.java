package com.nn.accountapp.mapper;

import com.nn.accountapp.model.dto.AccountDTO;
import com.nn.accountapp.model.dto.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateAccountRequestToAccountDTO {


    CreateAccountRequestToAccountDTO INSTANCE = Mappers.getMapper(CreateAccountRequestToAccountDTO.class);

    AccountDTO accountDtoToAccountEntity(CreateAccountRequest createAccountRequest);

}
