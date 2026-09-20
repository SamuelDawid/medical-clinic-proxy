package com.example.medicalclinicproxy.searchCriteria;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record AvailableAppointmentCriteria(
        Long doctorId,
        String specialization,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime from,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime to
) {
}
