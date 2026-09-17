package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentAlreadyTakenException extends MedicalClinicProxyException {
    public AppointmentAlreadyTakenException() {
        super("This Appointment is already taken", HttpStatus.CONFLICT);
    }
}
