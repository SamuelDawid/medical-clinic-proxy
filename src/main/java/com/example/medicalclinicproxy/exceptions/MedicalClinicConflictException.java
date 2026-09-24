package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class MedicalClinicConflictException extends MedicalClinicProxyException {
    public MedicalClinicConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
