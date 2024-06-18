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
import ru.omon4412.minibank.dto.ResponseAccountDto;
import ru.omon4412.minibank.util.Result;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        Result<String> result = accountServiceImpl.createAccount(newAccountDto, 1L);

        assertTrue(result.isSuccess());
        assertTrue(result.getOrNull().contains("Счёт успешно создан"));

        verify(middleServiceClient, times(1)).createAccount(any(NewAccountDto.class), any(Long.class));
    }

    @Test
    void test_createAccount_whenAccountAlreadyExists() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                409, "Conflict", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);

        Result<String> result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);

        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("У Вас уже есть счет"));
    }

    @Test
    void test_createAccount_whenUserNotRegisteredBeforeAccountCreating() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                404, "Not found", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);

        Result<String> result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);

        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сначала нужно зарегистрироваться."));
    }

    @Test
    void test_createAccount_whenServiceIsNotAvailable() {
        FeignException.InternalServerError feignClientException = new FeignException.InternalServerError(
                "Сервис недоступен. Пожалуйста, попробуйте позже.", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);

        Result<String> result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);

        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));
    }

    @Test
    void test_getAccounts_success() {
        Collection<ResponseAccountDto> responseAccountDtos = new ArrayList<>();
        ResponseAccountDto responseAccountDto = new ResponseAccountDto();
        responseAccountDto.setAccountName("Test");
        responseAccountDto.setAccountId("TestId");
        responseAccountDto.setAmount(5000L);
        responseAccountDtos.add(responseAccountDto);
        ResponseEntity<Collection<ResponseAccountDto>> responseEntity = ResponseEntity.ok(responseAccountDtos);
        when(middleServiceClient.getUserAccounts(any(Long.class))).thenReturn(responseEntity);

        Result<Collection<ResponseAccountDto>> result = accountServiceImpl.getUserAccounts(1L);

        assertTrue(result.isSuccess());
        assertEquals(result.getOrNull(), responseAccountDtos);
    }

    @Test
    void test_getAccounts_whenUserNotRegisteredBefore() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                404, "Not found", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.getUserAccounts(any(Long.class))).thenThrow(feignClientException);

        Result<Collection<ResponseAccountDto>> result = accountServiceImpl.getUserAccounts(1L);

        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сначала нужно зарегистрироваться."));
    }

    @Test
    void test_getAccounts_whenServiceIsNotAvailable() {
        FeignException.InternalServerError feignClientException = new FeignException.InternalServerError(
                "Сервис недоступен. Пожалуйста, попробуйте позже.", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.getUserAccounts(any(Long.class))).thenThrow(feignClientException);

        Result<Collection<ResponseAccountDto>> result = accountServiceImpl.getUserAccounts(1L);

        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));
    }
}