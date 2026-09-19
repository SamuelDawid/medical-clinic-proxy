package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends MedicalClinicProxyException {
    public ResourceNotFoundException(String resource) {
        super("Resource " + resource + " Not Found",HttpStatus.NOT_FOUND);
    }
}
