package com.github.vakho10.spring_javafx_boot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GreetingService {

    public String getGreeting() {
        log.info("Called getGreeting() service method");
        return "Welcome to Spring-JavaFX Application!";
    }
}
