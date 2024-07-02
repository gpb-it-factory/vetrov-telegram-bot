package ru.omon4412.minibank.service;

import ru.omon4412.minibank.dto.CreateTransferRequestDto;
import ru.omon4412.minibank.dto.TransferResponseDto;
import ru.omon4412.minibank.util.Result;

public interface TransferService {
    Result<TransferResponseDto> transfer(CreateTransferRequestDto createTransferRequestDto);
}
