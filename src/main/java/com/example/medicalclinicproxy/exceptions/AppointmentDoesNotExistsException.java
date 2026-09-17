package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentDoesNotExistsException extends MedicalClinicProxyException {
    public AppointmentDoesNotExistsException() {
        super("Appointment Does Not Exists", HttpStatus.NOT_FOUND);
    }
}
