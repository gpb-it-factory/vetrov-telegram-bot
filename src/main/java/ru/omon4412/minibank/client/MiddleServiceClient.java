package ru.omon4412.minibank.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import ru.omon4412.minibank.dto.UserRequestDto;

@FeignClient(name = "middleService", url = "${application.middleService.url}")
public interface MiddleServiceClient {

    @PostMapping("/users")
    ResponseEntity<Void> registerUser(UserRequestDto userRequestDto);
}
