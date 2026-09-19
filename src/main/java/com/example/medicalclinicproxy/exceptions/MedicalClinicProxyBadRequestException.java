package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class MedicalClinicProxyBadRequestException extends MedicalClinicProxyException {
    public MedicalClinicProxyBadRequestException(String message,HttpStatus status) {
        super(message, status);
    }
}
