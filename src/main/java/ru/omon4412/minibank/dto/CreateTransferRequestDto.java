package ru.omon4412.minibank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransferRequestDto {
    private String from;
    private String to;
    private double amount;
}
