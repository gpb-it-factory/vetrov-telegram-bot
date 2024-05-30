package ru.omon4412.minibank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.RegistrationResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class MiddleServiceGatewayImpl implements MiddleServiceGateway {

    private final UserRegistrationService userRegistrationService;

    @Override
    public RegistrationResult registerUser(UserRequestDto userRequestDto) {
        return userRegistrationService.registerUser(userRequestDto);
    }
}
