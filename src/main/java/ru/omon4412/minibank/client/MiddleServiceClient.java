package ru.omon4412.minibank.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.omon4412.minibank.dto.NewAccountDto;
import ru.omon4412.minibank.dto.ResponseAccountDto;
import ru.omon4412.minibank.dto.UserRequestDto;

import java.util.Collection;

@FeignClient(name = "middleService", url = "${application.middleService.url}")
public interface MiddleServiceClient {

    @PostMapping("/users")
    ResponseEntity<Void> registerUser(UserRequestDto userRequestDto);

    @PostMapping("/users/{id}/accounts")
    ResponseEntity<Void> createAccount(NewAccountDto newAccountDto, @PathVariable("id") Long userId);

    @GetMapping("/users/{id}/accounts")
    ResponseEntity<Collection<ResponseAccountDto>> getUserAccounts(@PathVariable("id") Long userId);
}
