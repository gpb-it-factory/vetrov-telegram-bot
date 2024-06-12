package ru.omon4412.minibank.service;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.model.ResponseResult;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final MiddleServiceClient middleServiceClient;

    @Override
    public ResponseResult createAccount(NewAccountDto newAccountDto, Long userId) {
        try {
            ResponseEntity<Void> response = middleServiceClient.createAccount(newAccountDto, userId);
            boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
            return new ResponseResult(isSuccessful, "Счёт успешно создан");
        } catch (FeignException.FeignClientException e) {
            return handleFeignException(e);
        } catch (RetryableException | FeignException.InternalServerError e) {
            return new ResponseResult(false, "Сервис недоступен. Пожалуйста, попробуйте позже.");
        }
    }

    private ResponseResult handleFeignException(FeignException.FeignClientException e) {
        if (e.status() == 409) {
            return new ResponseResult(false, "У Вас уже есть счет");
        }
        if (e.status() == 404) {
            return new ResponseResult(false, "Сначала нужно зарегистрироваться.");
        }
        return new ResponseResult(false, "Ошибка");
    }
}
