package ru.omon4412.minibank.business.service;

import ru.omon4412.minibank.business.dto.UserResponseDto;

import java.util.Optional;

public interface MiddleServiceGateway {

    Optional<Boolean> registerUser(UserResponseDto userResponseDto);
}
