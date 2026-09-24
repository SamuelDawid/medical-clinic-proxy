package com.example.medicalclinicproxy.exceptions.handler;

import com.example.medicalclinicproxy.dto.ErrorMessageDto;
import com.example.medicalclinicproxy.exceptions.MedicalClinicAuthException;
import com.example.medicalclinicproxy.exceptions.MedicalClinicProxyException;
import com.example.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
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
    @ExceptionHandler(MedicalClinicAuthException.class)
    public ResponseEntity<ErrorMessageDto> handleAuth(MedicalClinicAuthException exception) {
        log.error("Integration problem with medical-clinic: {}", exception.getMessage());
        return error(HttpStatus.BAD_GATEWAY, "Integration problem with medical-clinic");
    }

    @ExceptionHandler(MedicalClinicUnavailableException.class)
    public ResponseEntity<ErrorMessageDto> handleUnavailable(MedicalClinicUnavailableException exception) {
        log.error("Service unavailable: {}", exception.getMessage());
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable");
    }

    @ExceptionHandler(MedicalClinicProxyException.class)
    public ResponseEntity<ErrorMessageDto> handleProxyException(MedicalClinicProxyException exception) {
        log.warn("Rejected by medical-clinic: {} -> {}", exception.getStatus(), exception.getMessage());
        return error(exception.getStatus(), exception.getMessage());
    }

    private ResponseEntity<ErrorMessageDto> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorMessageDto(
                message,
                (long) status.value(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
    }
}
