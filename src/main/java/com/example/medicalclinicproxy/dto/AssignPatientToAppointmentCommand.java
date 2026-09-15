package com.example.medicalclinicproxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssignPatientToAppointmentCommand(
        @Schema(description = "Patient id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long patientId,
        @Schema(description = "Id of the free appointment to book", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long appointmentId
) {
}
