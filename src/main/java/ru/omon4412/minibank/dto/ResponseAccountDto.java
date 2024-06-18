package ru.omon4412.minibank.dto;

import lombok.Data;

@Data
public class ResponseAccountDto {
    private String accountId;
    private String accountName;
    private long amount;
}
