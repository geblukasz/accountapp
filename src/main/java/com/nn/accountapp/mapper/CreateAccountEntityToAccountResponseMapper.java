package com.nn.accountapp.mapper;

import com.nn.accountapp.model.response.CreateAccountResponse;
import com.nn.accountapp.model.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateAccountEntityToAccountResponseMapper {


    CreateAccountEntityToAccountResponseMapper INSTANCE = Mappers.getMapper(CreateAccountEntityToAccountResponseMapper.class);

    CreateAccountResponse accountDtoToAccountEntity(AccountEntity accountEntity);

}
