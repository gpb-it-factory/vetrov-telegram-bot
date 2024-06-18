package ru.omon4412.minibank.service;

import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.dto.ResponseAccountDto;
import ru.omon4412.minibank.util.Result;

import java.util.Collection;

public interface AccountService {
    Result<String> createAccount(NewAccountDto newAccountDto, Long userId);

    Collection<ResponseAccountDto> getUserAccounts(Long userId);
}
