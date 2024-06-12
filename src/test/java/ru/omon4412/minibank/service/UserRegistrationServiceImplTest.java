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
import ru.omon4412.minibank.dto.UserRequestDto;
import ru.omon4412.minibank.model.ResponseResult;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {
    @Mock
    private MiddleServiceClient middleServiceClient;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    @Test
    void test_registerUser_success() {
        UserRequestDto userRequestDto = new UserRequestDto();
        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        when(middleServiceClient.registerUser(any(UserRequestDto.class))).thenReturn(responseEntity);

        ResponseResult result = userRegistrationService.registerUser(userRequestDto);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Вы зарегистрированы!"));

        verify(middleServiceClient, times(1)).registerUser(any(UserRequestDto.class));
    }

    @Test
    void test_registerUser_whenUserAlreadyRegistered() {
        UserRequestDto userRequestDto = new UserRequestDto();
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                409, "Conflict", Request.create(Request.HttpMethod.POST, "/register", Map.of(), new byte[0],
                StandardCharsets.UTF_8), null, null);
        when(middleServiceClient.registerUser(any(UserRequestDto.class)))
                .thenThrow(feignClientException);

        ResponseResult result = userRegistrationService.registerUser(userRequestDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Вы уже зарегистрированы."));

        verify(middleServiceClient, times(1)).registerUser(any(UserRequestDto.class));
    }

    @Test
    void test_registerUser_when() {
        UserRequestDto userRequestDto = new UserRequestDto();
        ResponseEntity<Void> responseEntity = ResponseEntity.status(415).build();
        when(middleServiceClient.registerUser(any(UserRequestDto.class))).thenReturn(responseEntity);

        ResponseResult result = userRegistrationService.registerUser(userRequestDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Ошибка"));

        verify(middleServiceClient, times(1)).registerUser(any(UserRequestDto.class));
    }

    @Test
    void test_registerUser_whenServerIsDown() {
        UserRequestDto userRequestDto = new UserRequestDto();
        when(middleServiceClient.registerUser(any(UserRequestDto.class))).thenThrow(RetryableException.class);

        ResponseResult result = userRegistrationService.registerUser(userRequestDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));

        verify(middleServiceClient, times(1)).registerUser(any(UserRequestDto.class));
    }
}