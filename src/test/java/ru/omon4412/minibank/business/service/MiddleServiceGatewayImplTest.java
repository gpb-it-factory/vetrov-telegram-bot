package ru.omon4412.minibank.business.service;

import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.omon4412.minibank.business.dto.UserResponseDto;
import ru.omon4412.minibank.business.model.RegistrationResult;
import ru.omon4412.minibank.client.MiddleServiceClient;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiddleServiceGatewayImplTest {
    @Mock
    private MiddleServiceClient middleServiceClient;

    @InjectMocks
    private MiddleServiceGatewayImpl middleService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void test_registerUser_success() {
        UserResponseDto userResponseDto = new UserResponseDto();
        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        when(middleServiceClient.registerUser(any(UserResponseDto.class))).thenReturn(responseEntity);

        RegistrationResult result = middleService.registerUser(userResponseDto);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Вы зарегистрированы!"));

        verify(middleServiceClient, times(1)).registerUser(any(UserResponseDto.class));
    }

    @Test
    void test_registerUser_whenUserAlreadyRegistered() {
        UserResponseDto userResponseDto = new UserResponseDto();
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                409, "Conflict", Request.create(Request.HttpMethod.POST, "/register", Map.of(), new byte[0],
                StandardCharsets.UTF_8), null, null);
        when(middleServiceClient.registerUser(any(UserResponseDto.class)))
                .thenThrow(feignClientException);

        RegistrationResult result = middleService.registerUser(userResponseDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Вы уже зарегистрированы."));

        verify(middleServiceClient, times(1)).registerUser(any(UserResponseDto.class));
    }

    @Test
    void test_registerUser_when() {
        UserResponseDto userResponseDto = new UserResponseDto();
        ResponseEntity<Void> responseEntity = ResponseEntity.status(415).build();
        when(middleServiceClient.registerUser(any(UserResponseDto.class))).thenReturn(responseEntity);

        RegistrationResult result = middleService.registerUser(userResponseDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Ошибка"));

        verify(middleServiceClient, times(1)).registerUser(any(UserResponseDto.class));
    }

    @Test
    void test_registerUser_whenServerIsDown() {
        UserResponseDto userResponseDto = new UserResponseDto();
        when(middleServiceClient.registerUser(any(UserResponseDto.class))).thenThrow(RetryableException.class);

        RegistrationResult result = middleService.registerUser(userResponseDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Сервис недоступен. Пожалуйста, попробуйте позже."));

        verify(middleServiceClient, times(1)).registerUser(any(UserResponseDto.class));
    }
}