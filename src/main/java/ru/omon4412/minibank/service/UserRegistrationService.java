package ru.omon4412.minibank.service;

import ru.omon4412.minibank.dto.UserIdResponseDto;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.util.Result;

public interface UserRegistrationService {
    Result<String> registerUser(UserRequestDto userRequestDto);

    Result<UserIdResponseDto> getUserIdByUserName(String username);
}
