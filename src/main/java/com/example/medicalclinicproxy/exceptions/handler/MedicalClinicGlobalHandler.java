package com.example.medicalclinicproxy.exceptions.handler;

import com.example.medicalclinicproxy.dto.ErrorMessageDto;
import com.example.medicalclinicproxy.exceptions.*;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class MedicalClinicGlobalHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handleResourceNotFound(ResourceNotFoundException resourceNotFoundexception) {
        log.error("Resource not found: {} ", resourceNotFoundexception.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDto(
                        resourceNotFoundexception.getMessage(),
                        404L,
                        LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                ));
    }

    @ExceptionHandler(MedicalClinicAuthException.class)
    public ResponseEntity<ErrorMessageDto> handleAuthException(MedicalClinicAuthException exception) {
        log.error("Configuration Integration problem: {}", exception.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorMessageDto(
                        exception.getMessage(),
                        502L,
                        LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                ));
    }

    @ExceptionHandler(MedicalClinicProxyBadRequestException.class)
    public ResponseEntity<ErrorMessageDto> handleBadRequest(MedicalClinicProxyBadRequestException exception) {
        log.error("Bad Request: {}", exception.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(
                        "Bad Request",
                        400L,
                        LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                ));
    }

    @ExceptionHandler(MedicalClinicUnavailableException.class)
    public ResponseEntity<ErrorMessageDto> handleServiceUnavailable(MedicalClinicUnavailableException exception) {
        log.error("Service unavailable: {}", exception.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorMessageDto(
                        "Service Unavailable",
                        503L,
                        LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                ));
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorMessageDto> handleRetryable(RetryableException exception) {
        log.error("Waiting time exceeded: {}", exception.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new ErrorMessageDto(
                        "Waiting time exceeded",
                        504L,
                        LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                ));
    }
}
