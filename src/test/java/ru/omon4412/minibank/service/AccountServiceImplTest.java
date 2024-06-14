package ru.omon4412.minibank.service;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.model.ResponseResult;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private MiddleServiceClient middleServiceClient;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Test
    void test_createAccount_success() {
        NewAccountDto newAccountDto = new NewAccountDto();
        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class))).thenReturn(responseEntity);

        ResponseResult result = accountServiceImpl.createAccount(newAccountDto, 1L);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Счёт успешно создан"));

        verify(middleServiceClient, times(1)).createAccount(any(NewAccountDto.class), any(Long.class));
    }

    @Test
    void test_createAccount_whenAccountAlreadyExists() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                409, "Conflict", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);

        ResponseResult result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("У Вас уже есть счет"));
    }

    @Test
    void test_whenUserNotRegisteredBeforeAccountCreating() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                404, "Not found", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);
        ResponseResult result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Сначала нужно зарегистрироваться."));
    }

    @Test
    void test_whenServiceIsNotAvailable() {
        FeignException.InternalServerError feignClientException = new FeignException.InternalServerError(
                "Сервис недоступен. Пожалуйста, попробуйте позже.", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);
        ResponseResult result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));
    }
}