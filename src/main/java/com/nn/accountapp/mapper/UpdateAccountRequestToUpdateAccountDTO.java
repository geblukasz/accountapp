
package com.nn.accountapp.mapper;

import com.nn.accountapp.model.dto.UpdateAccountDTO;
import com.nn.accountapp.model.dto.UpdateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateAccountRequestToUpdateAccountDTO {

    UpdateAccountRequestToUpdateAccountDTO INSTANCE = Mappers.getMapper(UpdateAccountRequestToUpdateAccountDTO.class);

    UpdateAccountDTO accountDtoToAccountEntity(UpdateAccountRequest updateAccountRequest);

}
