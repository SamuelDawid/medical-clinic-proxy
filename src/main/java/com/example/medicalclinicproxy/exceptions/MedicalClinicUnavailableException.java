package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class MedicalClinicUnavailableException extends MedicalClinicProxyException {
    public MedicalClinicUnavailableException(String message, HttpStatus status) {
        super(message,status);
    }
}
