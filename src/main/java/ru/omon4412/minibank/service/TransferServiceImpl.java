package ru.omon4412.minibank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.omon4412.minibank.client.MiddleServiceClient;
import ru.omon4412.minibank.dto.CreateTransferRequestDto;
import ru.omon4412.minibank.dto.TransferResponseDto;
import ru.omon4412.minibank.model.ApiError;
import ru.omon4412.minibank.util.Result;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final MiddleServiceClient middleServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    public Result<TransferResponseDto> transfer(CreateTransferRequestDto createTransferRequestDto) {
        try {
            ResponseEntity<TransferResponseDto> response = middleServiceClient.transfer(createTransferRequestDto);
            return new Result.Success<>(response.getBody());
        } catch (FeignException.FeignClientException e) {
            return handleFeignException(e);
        } catch (RetryableException | FeignException.InternalServerError e) {
            return new Result.Failure<>(new Exception("Сервис недоступен. Пожалуйста, попробуйте позже."));
        }
    }

    private Result handleFeignException(FeignException.FeignClientException e) {
        ApiError apiError;
        try {
            apiError = objectMapper.readValue(e.contentUTF8(), ApiError.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        if (e.status() == 409) {
            return new Result.Failure<>(new Exception(apiError.getError()));
        }
        if (e.status() == 404) {
            return new Result.Failure<>(new Exception(apiError.getError()));
        }
        return new Result.Failure<>(new Exception("Ошибка"));
    }
}
