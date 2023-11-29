package com.nn.accountapp.mapper;

import com.nn.accountapp.model.dto.AccountDTO;
import com.nn.accountapp.model.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountEntityToAccountDTOMapper {


    AccountEntityToAccountDTOMapper INSTANCE = Mappers.getMapper(AccountEntityToAccountDTOMapper.class);

    AccountEntity accountDtoToAccountEntity(AccountDTO accountDTO);

}
