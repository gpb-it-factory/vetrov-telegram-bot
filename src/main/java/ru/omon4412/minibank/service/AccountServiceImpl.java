package ru.omon4412.minibank.service;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.dto.ResponseAccountDto;
import ru.omon4412.minibank.util.Result;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final MiddleServiceClient middleServiceClient;

    @Override
    public Result<String> createAccount(NewAccountDto newAccountDto, Long userId) {
        try {
            middleServiceClient.createAccount(newAccountDto, userId);
            return new Result.Success<>("Счёт успешно создан");
        } catch (FeignException.FeignClientException e) {
            return handleFeignException(e);
        } catch (RetryableException | FeignException.InternalServerError e) {
            return new Result.Failure<>(new Exception("Сервис недоступен. Пожалуйста, попробуйте позже."));
        }
    }

    @Override
    public Collection<ResponseAccountDto> getUserAccounts(Long userId) {
        return null;
    }

    private Result<String> handleFeignException(FeignException.FeignClientException e) {
        if (e.status() == 409) {
            return new Result.Failure<>(new Exception("У Вас уже есть счет"));
        }
        if (e.status() == 404) {
            return new Result.Failure<>(new Exception("Сначала нужно зарегистрироваться."));
        }
        return new Result.Failure<>(new Exception("Ошибка"));
    }
}
