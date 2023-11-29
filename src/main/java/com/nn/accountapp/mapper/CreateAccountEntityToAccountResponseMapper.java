package com.nn.accountapp.mapper;


import com.nn.accountapp.model.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.nn.accountapp.model.dto.CreateAccountResponse;

@Mapper
public interface CreateAccountEntityToAccountResponseMapper {


    CreateAccountEntityToAccountResponseMapper INSTANCE = Mappers.getMapper(CreateAccountEntityToAccountResponseMapper.class);

    CreateAccountResponse accountDtoToAccountEntity(AccountEntity accountEntity);

}
