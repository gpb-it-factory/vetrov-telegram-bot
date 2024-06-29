package ru.omon4412.minibank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAccountDto {
    private String accountId;
    private String accountName;
    private BigDecimal amount;
}
