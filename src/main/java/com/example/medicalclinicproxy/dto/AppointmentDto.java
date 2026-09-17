package com.example.medicalclinicproxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record AppointmentDto(
        @Schema(description = "Appointment id", example = "1")
        Long id,
        @Schema(description = "Start time and Date of the appointment, always a full quarter of an hour", example = "2026-08-10T9:15:00")
        LocalDateTime startDateTime,
        @Schema(description = "End time and Date of the appointment, always a full quarter of an hour", example = "2026-08-10T10:15:00")
        LocalDateTime endDateTime,
        @Schema(description = "Full name of the doctor conducting the appointment", example = "John Doe")
        String doctorId,
        @Schema(description = "Full name of the assigned patient, null when the appointment is still free", example = "Jane Roe")
        String patientId
) {
}
