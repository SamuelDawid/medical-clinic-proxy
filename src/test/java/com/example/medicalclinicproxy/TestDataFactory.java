package com.example.medicalclinicproxy;

import com.example.medicalclinicproxy.dto.DoctorDto;
import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PatientDto;
import com.example.medicalclinicproxy.dto.UserDto;

public final class TestDataFactory {
    public static final String CARDIOLOGY = "CARDIOLOGY";
    public static final String DERMATOLOGY = "DERMATOLOGY";
    public static final Long DOCTOR_ID = 1L;
    public static final String DOCTOR_NAME = "Anna Kowalska";
    public static final Long PATIENT_ID = 1L;
    public static final String PATIENT_NAME = "Piotr Nowak";

    private TestDataFactory() {
    }

    public static UserDto doctorUser() {
        return new UserDto(10L, "Anna", "Kowalska");
    }

    public static UserDto patientUser() {
        return new UserDto(20L, "Piotr", "Nowak");
    }

    public static DoctorDto doctor() {
        return new DoctorDto(DOCTOR_ID, "CARDIOLOGY", doctorUser());
    }

    public static PatientDto patient() {
        return new PatientDto(PATIENT_ID, patientUser());
    }

    public static DoctorSummaryDto doctorSummary() {
        return new DoctorSummaryDto("Anna", "Kowalska", "CARDIOLOGY");
    }
}


