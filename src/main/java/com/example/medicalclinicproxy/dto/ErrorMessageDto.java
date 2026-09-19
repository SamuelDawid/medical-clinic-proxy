package com.example.medicalclinicproxy.dto;

import java.time.LocalDateTime;

public record ErrorMessageDto(
        String message,
        Long code,
        LocalDateTime timeOfError
) {
}
