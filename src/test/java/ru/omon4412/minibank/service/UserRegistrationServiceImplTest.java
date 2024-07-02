package ru.omon4412.minibank.service;

import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.UserIdResponseDto;
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.util.Result;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {
    @Mock
    private MiddleServiceClient middleServiceClient;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    private static void assertGetUserIdSuccess(Result<UserIdResponseDto> result) {
        assertTrue(result.isSuccess());
        assertEquals(123L, (long) result.getOrNull().getUserId());
    }

    @Test
    void registerUser_success() {
        UserRequestDto userRequestDto = new UserRequestDto();
        configureMiddleServiceClientRegisterUser();

        Result<String> result = userRegistrationService.registerUser(userRequestDto);

        assertRegisterUserSuccess(result);
    }

    @Test
    void registerUser_failed_whenUserAlreadyRegistered() {
        UserRequestDto userRequestDto = new UserRequestDto();
        configureConflictExceptionWhenUserAlreadyRegistered();

        Result<String> result = userRegistrationService.registerUser(userRequestDto);

        assertRegisterUserFailedWhenUserAlreadyRegistered(result);
    }

    @Test
    void registerUser_failed_whenServerIsDown() {
        UserRequestDto userRequestDto = new UserRequestDto();
        when(middleServiceClient.registerUser(any(UserRequestDto.class))).thenThrow(RetryableException.class);

        Result<String> result = userRegistrationService.registerUser(userRequestDto);

        assertRegisterUserFailedWhenServerIsDown(result);
    }

    @Test
    void getUserIdByUserName_success() {
        ResponseEntity<UserIdResponseDto> responseEntity = ResponseEntity.ok(new UserIdResponseDto(123L));
        when(middleServiceClient.getUserIdByUserName(any(String.class))).thenReturn(responseEntity);

        Result<UserIdResponseDto> result = userRegistrationService.getUserIdByUserName("username");

        assertGetUserIdSuccess(result);
    }

    private void assertRegisterUserSuccess(Result<String> result) {
        assertTrue(result.isSuccess());
        assertTrue(result.getOrNull().contains("Вы зарегистрированы!"));
        verify(middleServiceClient, times(1)).registerUser(any(UserRequestDto.class));
    }

    private void configureMiddleServiceClientRegisterUser() {
        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        when(middleServiceClient.registerUser(any(UserRequestDto.class))).thenReturn(responseEntity);
    }

    private void assertRegisterUserFailedWhenUserAlreadyRegistered(Result<String> result) {
        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Вы уже зарегистрированы."));
        verify(middleServiceClient, times(1)).registerUser(any(UserRequestDto.class));
    }

    private void configureConflictExceptionWhenUserAlreadyRegistered() {
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                409, "Conflict", Request.create(Request.HttpMethod.POST, "/register", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);
        when(middleServiceClient.registerUser(any(UserRequestDto.class)))
                .thenThrow(feignClientException);
    }

    private void assertRegisterUserFailedWhenServerIsDown(Result<String> result) {
        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));
        verify(middleServiceClient, times(1)).registerUser(any(UserRequestDto.class));
    }
}