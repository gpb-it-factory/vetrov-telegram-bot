package ru.omon4412.minibank.service;

import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.ResponseResult;

public interface UserRegistrationService {
    ResponseResult registerUser(UserRequestDto userRequestDto);
}
