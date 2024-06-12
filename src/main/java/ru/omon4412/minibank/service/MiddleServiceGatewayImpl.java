package ru.omon4412.minibank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.ResponseResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class MiddleServiceGatewayImpl implements MiddleServiceGateway {

    private final UserRegistrationService userRegistrationService;
    private final AccountService accountService;

    @Override
    public ResponseResult registerUser(UserRequestDto userRequestDto) {
        return userRegistrationService.registerUser(userRequestDto);
    }

    @Override
    public ResponseResult createAccount(NewAccountDto newAccountDto, Long userId) {
        return accountService.createAccount(newAccountDto, userId);
    }
}
