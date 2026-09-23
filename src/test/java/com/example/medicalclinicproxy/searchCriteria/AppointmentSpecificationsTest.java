package com.example.medicalclinicproxy.searchCriteria;

import com.example.medicalclinicproxy.TestDataFactory;
import com.example.medicalclinicproxy.enums.Timeframe;
import com.example.medicalclinicproxy.model.Appointment;
import com.example.medicalclinicproxy.repository.AppointmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.medicalclinicproxy.TestDataFactory.*;
import static com.example.medicalclinicproxy.searchCriteria.AppointmentSpecifications.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AppointmentSpecificationsTest {

    @Autowired
    AppointmentRepository repository;
    List<Appointment> appointments;

    @BeforeEach
    void setUp() {
        appointments = TestDataFactory.threeAppointments();
        repository.saveAll(appointments);
    }

    @Test
    void hasPatient_WhenPatientIdGiven_ShouldMatchOnlyAppointmentsOfThatPatient() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        //When
        List<Appointment> result = repository.findAll(hasPatient(PATIENT_ID), pageable).getContent();
        //Then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(PATIENT_ID, result.getFirst().getPatientId()),
                () -> assertEquals(TestDataFactory.PATIENT_NAME, result.getFirst().getPatientName())
        );
    }


    @Test
    void hasDoctor_WhenDoctorIdGiven_ShouldMatchOnlyAppointmentsOfThatDoctor() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        //When
        List<Appointment> result = repository.findAll(hasDoctor(DOCTOR_ID), pageable).getContent();
        //Then
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(DOCTOR_ID, result.getFirst().getDoctorId()),
                () -> assertEquals(TestDataFactory.DOCTOR_NAME, result.getFirst().getDoctorName())
        );
    }

    @Test
    void hasSpecialization_WhenSpecializationGiven_ShouldMatchOnlyAppointmentsWithThatSpecialisation() {
        //When
        List<Appointment> result = repository.findAll(hasSpecialization(DERMATOLOGY));
        //Then
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(DERMATOLOGY, result.getFirst().getDoctorSpecialisation())
        );
    }

    @Test
    void startsBetween_WhenBothBoundsGiven_ShouldMatchAppointmentsStartingInsideTheRange() {
        //Given
        LocalDateTime from = LocalDateTime.of(2030, 9, 15, 11, 30);
        LocalDateTime to = LocalDateTime.of(2030, 9, 17, 16, 30);
        //When
        List<Appointment> result = repository.findAll(startsBetween(from, to));
        //Then
        assertAll(
                () -> assertEquals(3, result.size())
        );

    }

    @Test
    void startsBetween_WhenOnlyFromGiven_ShouldMatchAppointmentsStartingAtOrAfterIt() {
        //Given
        LocalDateTime from = LocalDateTime.of(2030, 9, 16, 9, 0);
        //When
        List<Appointment> result = repository.findAll(startsBetween(from, null));
        //Then
        assertEquals(2, result.size());
    }

    @Test
    void startsBetween_WhenOnlyToGiven_ShouldMatchAppointmentsStartingBeforeIt() {
        //Given
        LocalDateTime to = LocalDateTime.of(2030, 9, 17, 16, 30);
        //When
        List<Appointment> result = repository.findAll(startsBetween(null, to));
        //Then
        assertEquals(3, result.size());
    }

    @Test
    void inTimeframe_WhenPastGiven_ShouldMatchOnlyAlreadyFinishedAppointments() {
        //Given
        repository.saveAll(TestDataFactory.threeAppointmentsInThePast());
        //When
        List<Appointment> result = repository.findAll(inTimeframe(Timeframe.PAST));
        //Then
        assertEquals(2, result.size());
    }

    @Test
    void isFree_ShouldMatchOnlyAppointmentsWithoutPatient() {
        //When
        List<Appointment> result = repository.findAll(isFree());
        //Then
        assertEquals(1, result.size());

    }
}
