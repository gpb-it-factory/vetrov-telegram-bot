package ru.omon4412.minibank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAccountDto {
    private String accountId;
    private String accountName;
    private double amount;
}
