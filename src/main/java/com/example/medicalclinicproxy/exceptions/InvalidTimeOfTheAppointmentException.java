package com.example.medicalclinicproxy.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTimeOfTheAppointmentException extends MedicalClinicProxyException {
    public InvalidTimeOfTheAppointmentException() {
        super("Invalid Time Of The Appointment", HttpStatus.BAD_REQUEST);
    }

}
