package ru.omon4412.minibank.business.service;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.omon4412.minibank.business.dto.UserResponseDto;
import ru.omon4412.minibank.business.model.RegistrationResult;
import ru.omon4412.minibank.client.MiddleServiceClient;

@RequiredArgsConstructor
@Slf4j
public class MiddleServiceGatewayImpl implements MiddleServiceGateway {
    private final MiddleServiceClient middleServiceClient;

    @Override
    public RegistrationResult registerUser(UserResponseDto userResponseDto) {
        log.info("Регистрация пользователя {}", userResponseDto.getUserId());
        try {
            ResponseEntity<Void> response = middleServiceClient.registerUser(userResponseDto);
            boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
            log.info("Статус регистрации пользователя: {}", isSuccessful);
            return new RegistrationResult(isSuccessful, getRegistrationMessage(isSuccessful));
        } catch (FeignException.FeignClientException e) {
            log.warn("Ошибка при вызове MiddleServiceClient {}", e.getMessage());
            return handleFeignException(e);
        } catch (RetryableException e) {
            log.warn("Ошибка при вызове MiddleServiceClient {}", e.getMessage());
            return new RegistrationResult(false, "Сервис недоступен. Пожалуйста, попробуйте позже.");
        }
    }

    private String getRegistrationMessage(boolean isSuccessful) {
        return isSuccessful ? "Вы зарегистрированы!" : "Ошибка";
    }

    private RegistrationResult handleFeignException(FeignException.FeignClientException e) {
        if (e.status() == 409) {
            return new RegistrationResult(false, "Вы уже зарегистрированы.");
        }
        return new RegistrationResult(false, "Ошибка");
    }
}
