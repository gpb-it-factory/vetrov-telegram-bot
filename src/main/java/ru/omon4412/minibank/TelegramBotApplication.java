package ru.omon4412.minibank;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TelegramBotApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    @Override
    public void run(String... args) {

    }
}
