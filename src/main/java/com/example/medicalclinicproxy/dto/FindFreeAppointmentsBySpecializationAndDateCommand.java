package com.example.medicalclinicproxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record FindFreeAppointmentsBySpecializationAndDateCommand(
        @Schema(defaultValue = "Doctor Specialization", example = "Kardiolog")
        String specialization,
        @Schema(description = "Start date of the search",
                example = "2026-08-10T10:15:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startDate,
        @Schema(description = "End date of the search",
                example = "2026-08-10T16:15:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endDate
) {
}
