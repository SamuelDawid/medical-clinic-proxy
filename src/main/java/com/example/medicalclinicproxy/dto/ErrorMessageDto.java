package com.example.medicalclinicproxy.dto;

public record ErrorMessageDto(
        String message,
        Long code,
        String timeOfError
) {
}
