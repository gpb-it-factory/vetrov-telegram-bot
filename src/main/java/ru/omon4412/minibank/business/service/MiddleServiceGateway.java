package ru.omon4412.minibank.business.service;

import ru.omon4412.minibank.business.dto.UserResponseDto;
import ru.omon4412.minibank.business.model.RegistrationResult;

public interface MiddleServiceGateway {

    RegistrationResult registerUser(UserResponseDto userResponseDto);
}
