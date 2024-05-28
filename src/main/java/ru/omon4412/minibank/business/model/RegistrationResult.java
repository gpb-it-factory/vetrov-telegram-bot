package ru.omon4412.minibank.business.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegistrationResult {
    private boolean success;
    private String message;
}
