package ru.omon4412.minibank.business.service;

import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.business.dto.UserResponseDto;

import java.util.Optional;

@RequiredArgsConstructor
public class MiddleServiceGatewayImpl implements MiddleServiceGateway {
    private final MiddleServiceClient middleServiceClient;

    @Override
    public Optional<Boolean> registerUser(UserResponseDto userResponseDto) {
        try {
            return Optional.ofNullable(middleServiceClient.registerUser(userResponseDto));
        } catch (RetryableException e) {
            return Optional.empty();
        }
    }
}
