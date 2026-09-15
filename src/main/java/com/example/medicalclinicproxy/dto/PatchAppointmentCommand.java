package com.example.medicalclinicproxy.dto;

import java.time.LocalDateTime;

public record PatchAppointmentCommand(
        Long appointmentId,
        LocalDateTime time
) {
}
