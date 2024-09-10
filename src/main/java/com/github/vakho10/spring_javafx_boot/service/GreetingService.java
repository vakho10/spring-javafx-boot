package com.github.vakho10.spring_javafx_boot.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String getGreeting() {
        return "Welcome to Spring-JavaFX Application!";
    }
}
