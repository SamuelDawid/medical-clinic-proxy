package com.example.medicalclinicproxy.dto;

public record DoctorDto(
        Long id,
        String medicalSpecialty,
        UserDto userDto
) {
}
