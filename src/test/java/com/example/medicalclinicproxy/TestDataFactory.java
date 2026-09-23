package com.example.medicalclinicproxy;

import com.example.medicalclinicproxy.dto.AppointmentDto;
import com.example.medicalclinicproxy.dto.AvailableAppointmentSummary;
import com.example.medicalclinicproxy.dto.DoctorDto;
import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PatientDto;
import com.example.medicalclinicproxy.dto.UserDto;
import com.example.medicalclinicproxy.model.Appointment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class TestDataFactory {
    public static final String CARDIOLOGY = "CARDIOLOGY";
    public static final String DERMATOLOGY = "DERMATOLOGY";

    public static final Long DOCTOR_ID = 1L;
    public static final String DOCTOR_NAME = "Anna Kowalska";
    public static final Long PATIENT_ID = 1L;
    public static final String PATIENT_NAME = "Piotr Nowak";

    public static final LocalDateTime FIRST_START = LocalDateTime.of(2030, 9, 15, 15, 30);
    public static final LocalDateTime FIRST_END = LocalDateTime.of(2030, 9, 15, 16, 15);
    public static final LocalDateTime SECOND_START = LocalDateTime.of(2030, 9, 16, 9, 15);
    public static final LocalDateTime SECOND_END = LocalDateTime.of(2030, 9, 16, 10, 0);
    public static final LocalDateTime THIRD_START = LocalDateTime.of(2030, 9, 17, 11, 0);
    public static final LocalDateTime THIRD_END = LocalDateTime.of(2030, 9, 17, 11, 45);

    private TestDataFactory() {
    }

    public static UserDto doctorUser() {
        return new UserDto(10L, "Anna", "Kowalska");
    }

    public static UserDto patientUser() {
        return new UserDto(20L, "Piotr", "Nowak");
    }

    public static DoctorDto doctor() {
        return new DoctorDto(DOCTOR_ID, CARDIOLOGY, doctorUser());
    }

    public static PatientDto patient() {
        return new PatientDto(PATIENT_ID, patientUser());
    }

    public static DoctorSummaryDto doctorSummary() {
        return new DoctorSummaryDto("Anna", "Kowalska", CARDIOLOGY);
    }

    public static List<Appointment> threeAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(appointment( FIRST_START, FIRST_END, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOCTOR_NAME, CARDIOLOGY));
        appointments.add(appointment( SECOND_START, SECOND_END, 2L, "Maria Wisniewska", DOCTOR_ID, DOCTOR_NAME, CARDIOLOGY));
        appointments.add(appointment( THIRD_START, THIRD_END, null, null, 2L, "Jan Nowicki", DERMATOLOGY));
        return appointments;
    }
    public static List<Appointment> twoAppointmentsInThePast(){
        LocalDateTime startFirstInThePast = LocalDateTime.of(2022,12,5,9,15);
        LocalDateTime endFirstInThePast = LocalDateTime.of(2022,12,5,10,15);
        LocalDateTime startSecondInThePast = LocalDateTime.of(2022,12,5,9,15);
        LocalDateTime endSecondInThePast = LocalDateTime.of(2022,12,5,10,15);
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(appointment( startFirstInThePast, endFirstInThePast, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOCTOR_NAME, CARDIOLOGY));
        appointments.add(appointment( startSecondInThePast, endSecondInThePast, 2L, "Maria Wisniewska", DOCTOR_ID, DOCTOR_NAME, CARDIOLOGY));
        return appointments;
    }
    public static List<AppointmentDto> threeAppointmentDtos() {
        return List.of(
                new AppointmentDto(1L, FIRST_START, FIRST_END, DOCTOR_NAME, PATIENT_NAME),
                new AppointmentDto(2L, SECOND_START, SECOND_END, DOCTOR_NAME, "Maria Wisniewska"),
                new AppointmentDto(3L, THIRD_START, THIRD_END, "Jan Nowicki", null)
        );
    }

    public static List<AppointmentDto> threeAppointmentsForTheSamePatient(String patientName) {
        return List.of(
                new AppointmentDto(1L, FIRST_START, FIRST_END, DOCTOR_NAME, patientName),
                new AppointmentDto(2L, SECOND_START, SECOND_END, DOCTOR_NAME, patientName),
                new AppointmentDto(3L, THIRD_START, THIRD_END, "Jan Nowicki", patientName)
        );
    }

    public static List<AvailableAppointmentSummary> threeAvailableAppointments() {
        return List.of(
                new AvailableAppointmentSummary(FIRST_START, FIRST_END, DOCTOR_NAME),
                new AvailableAppointmentSummary(SECOND_START, SECOND_END, DOCTOR_NAME),
                new AvailableAppointmentSummary(THIRD_START, THIRD_END, "Jan Nowicki")
        );
    }


    public static Appointment bookedAppointment() {
        return appointment( FIRST_START, FIRST_END, PATIENT_ID, PATIENT_NAME, DOCTOR_ID, DOCTOR_NAME, CARDIOLOGY);
    }

    public static Appointment freeAppointment() {
        return appointment( FIRST_START, FIRST_END, null, null, DOCTOR_ID, DOCTOR_NAME, CARDIOLOGY);
    }

    public static Appointment appointment(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Long patientId,
            String patientName,
            Long doctorId,
            String doctorName,
            String doctorSpecialisation) {
        return new Appointment(
                null,
                startDateTime,
                endDateTime,
                patientId,
                doctorId,
                doctorName,
                patientName,
                doctorSpecialisation
        );
    }
}
