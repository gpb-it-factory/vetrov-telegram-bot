package ru.omon4412.minibank.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import ru.omon4412.minibank.business.dto.UserResponseDto;

@FeignClient(name = "middleService", url = "${ru.omon4412.services.middleservice.url}")
public interface MiddleServiceClient {

    @PostMapping("/users")
    ResponseEntity<Void> registerUser(UserResponseDto userResponseDto);
}
