package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDateOfAppointmentException extends MedicalClinicProxyException {
    public InvalidDateOfAppointmentException() {
        super("Date must be in the future", HttpStatus.BAD_REQUEST);
    }
}
