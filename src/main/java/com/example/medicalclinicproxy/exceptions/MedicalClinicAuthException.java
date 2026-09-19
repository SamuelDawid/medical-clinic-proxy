package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class MedicalClinicAuthException extends MedicalClinicProxyException {
    public MedicalClinicAuthException(String message, HttpStatus status) {
        super(message,status);
    }
}
