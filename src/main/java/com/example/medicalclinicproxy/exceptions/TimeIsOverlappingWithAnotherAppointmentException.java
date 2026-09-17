package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class TimeIsOverlappingWithAnotherAppointmentException extends MedicalClinicProxyException {
    public TimeIsOverlappingWithAnotherAppointmentException() {
        super("Another appointment at this time already exists", HttpStatus.CONFLICT);
    }
}
