package com.example.medicalclinicproxy.dto;

public record DoctorSummaryDto(
        String firstName,
        String lastName,
        String medicalSpecialty
) {
}
