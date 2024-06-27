package ru.omon4412.minibank.service;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.UserIdResponseDto;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.util.Result;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final MiddleServiceClient middleServiceClient;

    @Override
    public Result<String> registerUser(UserRequestDto userRequestDto) {
        log.info("Регистрация пользователя {}", userRequestDto.getUserId());
        try {
            ResponseEntity<Void> response = middleServiceClient.registerUser(userRequestDto);
            boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
            log.info("Статус регистрации пользователя: {}", isSuccessful);
            return new Result.Success<>(getRegistrationMessage(isSuccessful));
        } catch (FeignException.FeignClientException e) {
            log.warn("Ошибка при вызове MiddleServiceClient {}", e.getMessage());
            return handleFeignException(e);
        } catch (RetryableException | FeignException.InternalServerError e) {
            log.warn("Ошибка при вызове MiddleServiceClient {}", e.getMessage());
            return new Result.Failure<>(new Throwable("Сервис недоступен. Пожалуйста, попробуйте позже."));
        }
    }

    @Override
    public Result<UserIdResponseDto> getUserIdByUserName(String username) {
        try {
            ResponseEntity<UserIdResponseDto> response = middleServiceClient.getUserIdByUserName(username);
            UserIdResponseDto userIdResponseDto = response.getBody();
            if (userIdResponseDto == null) {
                return new Result.Failure<>(new Throwable("Пользователь не зарегистрирован"));
            }
            return new Result.Success<>(userIdResponseDto);
        } catch (FeignException.FeignClientException e) {
            return handleFeignException(e);
        } catch (RetryableException | FeignException.InternalServerError e) {
            return new Result.Failure<>(new Throwable("Сервис недоступен. Пожалуйста, попробуйте позже."));
        }
    }

    private String getRegistrationMessage(boolean isSuccessful) {
        return isSuccessful ? "Вы зарегистрированы!" : "Ошибка";
    }

    private Result handleFeignException(FeignException.FeignClientException e) {
        if (e.status() == 409) {
            return new Result.Failure<>(new Throwable("Вы уже зарегистрированы."));
        }
        return new Result.Failure<>(new Throwable("Ошибка"));
    }
}
