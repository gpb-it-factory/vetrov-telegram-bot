package ru.omon4412.minibank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.omon4412.minibank.dto.*;
import ru.omon4412.minibank.util.Result;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class MiddleServiceGatewayImpl implements MiddleServiceGateway {

    private final UserRegistrationService userRegistrationService;
    private final AccountService accountService;
    private final TransferService transferService;

    @Override
    public Result<String> registerUser(UserRequestDto userRequestDto) {
        return userRegistrationService.registerUser(userRequestDto);
    }

    @Override
    public Result<UserIdResponseDto> getUserIdByUserName(String username) {
        return userRegistrationService.getUserIdByUserName(username);
    }

    @Override
    public Result<String> createAccount(NewAccountDto newAccountDto, Long userId) {
        return accountService.createAccount(newAccountDto, userId);
    }

    @Override
    public Result<Collection<ResponseAccountDto>> getUserAccounts(Long userId) {
        return accountService.getUserAccounts(userId);
    }

    @Override
    public Result<TransferResponseDto> transfer(CreateTransferRequestDto createTransferRequestDto) {
        return transferService.transfer(createTransferRequestDto);
    }
}
