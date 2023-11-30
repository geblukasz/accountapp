package com.nn.accountapp.service;

import com.nn.accountapp.exception.AccountNotFoundException;
import com.nn.accountapp.mapper.AccountEntityToAccountDTOMapper;
import com.nn.accountapp.mapper.AccountEntityToAccountInfoResponseMapper;
import com.nn.accountapp.mapper.CreateAccountEntityToAccountResponseMapper;
import com.nn.accountapp.model.dto.AccountDTO;
import com.nn.accountapp.model.response.AccountInfoResponse;
import com.nn.accountapp.model.response.CreateAccountResponse;
import com.nn.accountapp.model.entity.AccountEntity;
import com.nn.accountapp.model.entity.SubAccountEntity;
import com.nn.accountapp.model.enumeration.AllowedCurrency;
import com.nn.accountapp.repository.AccountRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.nn.accountapp.model.enumeration.AllowedCurrency.PLN;

@Service
@RequiredArgsConstructor
public class AccountService {

    AccountEntityToAccountDTOMapper accountDtoToAccountEntityMapper = AccountEntityToAccountDTOMapper.INSTANCE;
    CreateAccountEntityToAccountResponseMapper createAccountEntityToAccountResponseMapper = CreateAccountEntityToAccountResponseMapper.INSTANCE;
    AccountEntityToAccountInfoResponseMapper accountEntityToAccountInfoResponseMapper = AccountEntityToAccountInfoResponseMapper.INSTANCE;

    private final AccountRepository accountRepository;

    public CreateAccountResponse createAccount(@NotNull final AccountDTO accountDTO) {
        AccountEntity accountEntity = accountDtoToAccountEntityMapper.accountDtoToAccountEntity(accountDTO);
        accountEntity.setIdentificationNumber(UUID.randomUUID());
        SubAccountEntity subAccountEntity = SubAccountEntity.builder()
                .currencyCode(PLN)
                .amount(accountDTO.getAmount())
                .account(accountEntity)
                .build();
        accountEntity.setSubAccounts(List.of(subAccountEntity));
        AccountEntity savedAccountEntity = accountRepository.save(accountEntity);
        return createAccountEntityToAccountResponseMapper.accountDtoToAccountEntity(savedAccountEntity);
    }

    public void addSubAccountIfRequired(@NotNull final AllowedCurrency currencyCodeTo, @NotNull final List<SubAccountEntity> subAccountEntities, @NotNull final AccountEntity accountEntity) {
        Optional<SubAccountEntity> subAccountEntityOptional = subAccountEntities.stream()
                .filter(currencyAmountEntity -> currencyAmountEntity.getCurrencyCode().equals(currencyCodeTo))
                .findFirst();
        if (subAccountEntityOptional.isEmpty()) {
            SubAccountEntity currencyAmountEntity = SubAccountEntity.builder()
                    .currencyCode(currencyCodeTo)
                    .amount(BigDecimal.ZERO)
                    .account(accountEntity)
                    .build();
            accountEntity.getSubAccounts().add(currencyAmountEntity);
        }
    }

    public AccountInfoResponse getAccount(@NotNull final UUID identificationNumber) throws AccountNotFoundException {
        AccountEntity accountEntity = accountRepository.findByIdentificationNumber(identificationNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account " + identificationNumber + " not found"));
        AccountInfoResponse accountInfoResponse = accountEntityToAccountInfoResponseMapper.mapAccountEntityToAccountInfoResponse(accountEntity);
        return accountInfoResponse;
    }
}

