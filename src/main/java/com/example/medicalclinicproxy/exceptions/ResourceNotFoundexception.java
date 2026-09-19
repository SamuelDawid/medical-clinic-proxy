package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundexception extends MedicalClinicProxyException {
    public ResourceNotFoundexception(String resource) {
        super("Resource " + resource + " Not Found",HttpStatus.NOT_FOUND);
    }
}
