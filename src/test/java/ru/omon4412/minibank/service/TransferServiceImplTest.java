package ru.omon4412.minibank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.CreateTransferRequestDto;
import ru.omon4412.minibank.dto.TransferResponseDto;
import ru.omon4412.minibank.model.ApiError;
import ru.omon4412.minibank.util.Result;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private MiddleServiceClient middleServiceClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransferServiceImpl transferServiceImpl;

    @Test
    void test_transfer_success() {
        CreateTransferRequestDto createTransferRequestDto = new CreateTransferRequestDto();
        TransferResponseDto transferResponseDto = new TransferResponseDto();
        transferResponseDto.setTransferId("123-123");
        ResponseEntity<TransferResponseDto> responseEntity = ResponseEntity.ok(transferResponseDto);
        when(middleServiceClient.transfer(any(CreateTransferRequestDto.class))).thenReturn(responseEntity);

        Result<TransferResponseDto> result = transferServiceImpl.transfer(createTransferRequestDto);

        assertTrue(result.isSuccess());
        assertTrue(result.getOrNull().getTransferId().contains("123-123"));
    }

    @Test
    void test_transfer_whenAccountDoesNotExist() throws JsonProcessingException {
        CreateTransferRequestDto createTransferRequestDto = new CreateTransferRequestDto();
        createTransferRequestDto.setFrom("1");
        createTransferRequestDto.setTo("2");
        createTransferRequestDto.setAmount(new BigDecimal(50));
        ApiError apiError = new ApiError("Счёт не найден", "/transfers", 404, ZonedDateTime.now());
        String errorBody = "{\"error\":\"Пользователь не найден\"}";
        when(objectMapper.readValue(errorBody, ApiError.class)).thenReturn(apiError);
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                404, "Not found", Request.create(Request.HttpMethod.POST, "/", Collections.emptyMap(), null,
                StandardCharsets.UTF_8, null), errorBody.getBytes(StandardCharsets.UTF_8), null);
        when(middleServiceClient.transfer(any(CreateTransferRequestDto.class))).thenThrow(feignClientException);

        Result<TransferResponseDto> result = transferServiceImpl.transfer(createTransferRequestDto);

        assertTrue(result.isFailure());
        assertTrue(result.exceptionOrNull().getMessage().contains("Счёт не найден"));
    }

    @Test
    void test_transfer_whenUserDoesNotExist() throws JsonProcessingException {
        CreateTransferRequestDto createTransferRequestDto = new CreateTransferRequestDto("user1", "user2", new BigDecimal(100));

        ApiError apiError = new ApiError("Пользователь не найден", "/transfers", 404, ZonedDateTime.now());
        String errorBody = "{\"error\":\"Пользователь не найден\"}";
        when(objectMapper.readValue(errorBody, ApiError.class)).thenReturn(apiError);

        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                404, "Not found", Request.create(Request.HttpMethod.POST, "/", Collections.emptyMap(),
                null, StandardCharsets.UTF_8, null), errorBody.getBytes(StandardCharsets.UTF_8), null);

        when(middleServiceClient.transfer(any(CreateTransferRequestDto.class))).thenThrow(feignClientException);

        Result<TransferResponseDto> result = transferServiceImpl.transfer(createTransferRequestDto);

        assertTrue(result.isFailure());
        assertEquals("Пользователь не найден", result.exceptionOrNull().getMessage());
    }

    @Test
    void test_transfer_whenServerIsNotAvailable() {
        CreateTransferRequestDto createTransferRequestDto = new CreateTransferRequestDto("user1", "user2", new BigDecimal(100));

        FeignException.InternalServerError feignClientException = new FeignException.InternalServerError(
                "Сервис недоступен. Пожалуйста, попробуйте позже.", Request.create(Request.HttpMethod.POST, "/users/1/accounts", Map.of(), new byte[0],
                StandardCharsets.UTF_8, null), null, null);

        when(middleServiceClient.transfer(any(CreateTransferRequestDto.class))).thenThrow(feignClientException);

        Result<TransferResponseDto> result = transferServiceImpl.transfer(createTransferRequestDto);

        assertTrue(result.isFailure());
        assertEquals("Сервис недоступен. Пожалуйста, попробуйте позже.", result.exceptionOrNull().getMessage());
    }
}