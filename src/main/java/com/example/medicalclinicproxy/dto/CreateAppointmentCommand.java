package com.example.medicalclinicproxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CreateAppointmentCommand(
        @Schema(description = "Doctor id", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Long doctorId,
        @Schema(description = "Start of the appointment, must be in the future and start at a full quarter of an hour",
                example = "2026-08-10T10:15:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startDateTime,
        @Schema(description = "end of the appointment", example = "2026-08-10T11:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endDateTime
) {
}
