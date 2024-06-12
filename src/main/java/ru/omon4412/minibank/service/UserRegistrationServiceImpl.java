package ru.omon4412.minibank.service;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.ResponseResult;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final MiddleServiceClient middleServiceClient;

    @Override
    public ResponseResult registerUser(UserRequestDto userRequestDto) {
        log.info("Регистрация пользователя {}", userRequestDto.getUserId());
        try {
            ResponseEntity<Void> response = middleServiceClient.registerUser(userRequestDto);
            boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
            log.info("Статус регистрации пользователя: {}", isSuccessful);
            return new ResponseResult(isSuccessful, getRegistrationMessage(isSuccessful));
        } catch (FeignException.FeignClientException e) {
            log.warn("Ошибка при вызове MiddleServiceClient {}", e.getMessage());
            return handleFeignException(e);
        } catch (RetryableException | FeignException.InternalServerError e) {
            log.warn("Ошибка при вызове MiddleServiceClient {}", e.getMessage());
            return new ResponseResult(false, "Сервис недоступен. Пожалуйста, попробуйте позже.");
        }
    }

    private String getRegistrationMessage(boolean isSuccessful) {
        return isSuccessful ? "Вы зарегистрированы!" : "Ошибка";
    }

    private ResponseResult handleFeignException(FeignException.FeignClientException e) {
        if (e.status() == 409) {
            return new ResponseResult(false, "Вы уже зарегистрированы.");
        }
        return new ResponseResult(false, "Ошибка");
    }
}
