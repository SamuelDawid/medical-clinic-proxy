package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicProxyException {
    public PatientNotFoundException(Long id) {
        super("Patient with id: " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
