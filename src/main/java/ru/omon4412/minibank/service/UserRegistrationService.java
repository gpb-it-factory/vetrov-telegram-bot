package ru.omon4412.minibank.service;

import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.RegistrationResult;

public interface UserRegistrationService {
    RegistrationResult registerUser(UserRequestDto userRequestDto);
}
