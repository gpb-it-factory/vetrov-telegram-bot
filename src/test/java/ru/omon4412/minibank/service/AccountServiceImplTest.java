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

import java.math.BigDecimal;
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

    private static void assertCreateAccountFailedWhenAccountAlreadyExists(Result<String> result) {
        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("У Вас уже есть счет"));
    }

    private static void assertCreateAccountFailedWhenUserNotRegistered(Result<String> result) {
        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сначала нужно зарегистрироваться."));
    }

    private static void assertCreateAccountFailedWhenServerIsDown(Result<String> result) {
        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));
    }

    private static void assertUserGetAccountsSuccess(Result<Collection<ResponseAccountDto>> result, Collection<ResponseAccountDto> validCollectionResponseAccountDtos) {
        assertTrue(result.isSuccess());
        assertEquals(result.getOrNull(), validCollectionResponseAccountDtos);
    }

    private static void assertUserGetAccountsFailedWhenUserNotRegisteredBefore(Result<Collection<ResponseAccountDto>> result) {
        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сначала нужно зарегистрироваться."));
    }

    private static void assertUserGetAccountsFailedWhenServerIsDown(Result<Collection<ResponseAccountDto>> result) {
        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));
    }

    @Test
    void createAccount_success() {
        NewAccountDto newAccountDto = new NewAccountDto();
        configureMiddleServiceClientCreateAccount();

        Result<String> result = accountServiceImpl.createAccount(newAccountDto, 1L);

        assertCreateAccountSuccessCreateAccount(result);
    }

    @Test
    void createAccount_failed_whenAccountAlreadyExists() {
        configureConflictExceptionWhenAccountAlreadyExists();

        Result<String> result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);

        assertCreateAccountFailedWhenAccountAlreadyExists(result);
    }

    @Test
    void createAccount_failed_whenUserNotRegisteredBeforeAccountCreating() {
        configureNotFoundExceptionWhenUserNotRegistered();

        Result<String> result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);

        assertCreateAccountFailedWhenUserNotRegistered(result);
    }

    @Test
    void createAccount_failed_whenServiceIsNotAvailable() {
        configureCreateAccountInternalErrorWhenServerIsDown();

        Result<String> result = accountServiceImpl.createAccount(new NewAccountDto(), 1L);

        assertCreateAccountFailedWhenServerIsDown(result);
    }

    @Test
    void userGetAccounts_success() {
        Collection<ResponseAccountDto> validCollectionResponseAccountDtos = getValidCollectionResponseAccountDtos();
        ResponseEntity<Collection<ResponseAccountDto>> responseEntity = ResponseEntity.ok(validCollectionResponseAccountDtos);
        when(middleServiceClient.getUserAccounts(any(Long.class))).thenReturn(responseEntity);

        Result<Collection<ResponseAccountDto>> result = accountServiceImpl.getUserAccounts(1L);

        assertUserGetAccountsSuccess(result, validCollectionResponseAccountDtos);
    }

    @Test
    void userGetAccounts_failed_whenUserNotRegisteredBefore() {
        configureNotFoundExceptionWhenUserNotRegisteredBefore();

        Result<Collection<ResponseAccountDto>> result = accountServiceImpl.getUserAccounts(1L);

        assertUserGetAccountsFailedWhenUserNotRegisteredBefore(result);
    }

    @Test
    void userGetAccounts_whenServiceIsNotAvailable() {
        configureUserGetAccountsInternalErrorWhenServerIsDown();

        Result<Collection<ResponseAccountDto>> result = accountServiceImpl.getUserAccounts(1L);

        assertUserGetAccountsFailedWhenServerIsDown(result);
    }

    private void configureUserGetAccountsInternalErrorWhenServerIsDown() {
        FeignException.InternalServerError feignClientException = new FeignException.InternalServerError(
                "Сервис недоступен. Пожалуйста, попробуйте позже.", Request.create(Request.HttpMethod.POST,
                "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.getUserAccounts(any(Long.class))).thenThrow(feignClientException);
    }

    private void configureNotFoundExceptionWhenUserNotRegisteredBefore() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                404, "Not found", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.getUserAccounts(any(Long.class))).thenThrow(feignClientException);
    }

    private Collection<ResponseAccountDto> getValidCollectionResponseAccountDtos() {
        Collection<ResponseAccountDto> responseAccountDtos = new ArrayList<>();
        ResponseAccountDto responseAccountDto = new ResponseAccountDto();
        responseAccountDto.setAccountName("Test");
        responseAccountDto.setAccountId("TestId");
        responseAccountDto.setAmount(new BigDecimal(5000));
        responseAccountDtos.add(responseAccountDto);
        return responseAccountDtos;
    }

    private void configureCreateAccountInternalErrorWhenServerIsDown() {
        FeignException.InternalServerError feignClientException = new FeignException.InternalServerError(
                "Сервис недоступен. Пожалуйста, попробуйте позже.", Request.create(Request.HttpMethod.POST,
                "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);
    }

    private void configureNotFoundExceptionWhenUserNotRegistered() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                404, "Not found", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);
    }

    private void assertCreateAccountSuccessCreateAccount(Result<String> result) {
        assertTrue(result.isSuccess());
        assertTrue(result.getOrNull().contains("Счёт успешно создан"));
        verify(middleServiceClient, times(1)).createAccount(any(NewAccountDto.class), any(Long.class));
    }

    private void configureMiddleServiceClientCreateAccount() {
        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class))).thenReturn(responseEntity);
    }

    private void configureConflictExceptionWhenAccountAlreadyExists() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                409, "Conflict", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.createAccount(any(NewAccountDto.class), any(Long.class)))
                .thenThrow(feignClientException);
    }
}