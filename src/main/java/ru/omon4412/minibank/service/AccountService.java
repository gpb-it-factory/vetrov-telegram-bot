package ru.omon4412.minibank.service;

import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.model.ResponseResult;

public interface AccountService {
    ResponseResult createAccount(NewAccountDto newAccountDto, Long userId);
}
