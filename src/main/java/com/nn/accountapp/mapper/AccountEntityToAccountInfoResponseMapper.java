package com.nn.accountapp.mapper;

import com.nn.accountapp.model.entity.AccountEntity;
import com.nn.accountapp.model.response.AccountInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountEntityToAccountInfoResponseMapper {


    AccountEntityToAccountInfoResponseMapper INSTANCE = Mappers.getMapper(AccountEntityToAccountInfoResponseMapper.class);

    AccountInfoResponse mapAccountEntityToAccountInfoResponse(AccountEntity accountEntity);

}
