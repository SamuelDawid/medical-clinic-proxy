package com.example.medicalclinicproxy.searchCriteria;

import com.example.medicalclinicproxy.enums.Timeframe;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record AppointmentSearchCriteria(
        Long patientId,
        Long doctorId,
        String specialization,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime to,
        Timeframe timeframe
) {
}
