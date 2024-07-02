package ru.omon4412.minibank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransferRequestDto {
    private String from;
    private String to;
    private BigDecimal amount;
}
