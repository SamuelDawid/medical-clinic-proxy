package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class MedicalClinicProxyException extends RuntimeException {
    private final HttpStatus status;
    public MedicalClinicProxyException(String message,HttpStatus status) {
        super(message);
        this.status = status;
    }
}
