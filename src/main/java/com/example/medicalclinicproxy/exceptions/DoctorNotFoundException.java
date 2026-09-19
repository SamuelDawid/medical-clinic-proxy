package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicProxyException {
    public DoctorNotFoundException(Long id) {
        super("Doctor with id: " + id+ " not found", HttpStatus.NOT_FOUND);
    }
}
