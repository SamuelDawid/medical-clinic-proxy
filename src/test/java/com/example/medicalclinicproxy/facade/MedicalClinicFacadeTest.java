package com.example.medicalclinicproxy.facade;

import com.example.medicalclinicproxy.client.MedicalClinicClient;
import com.example.medicalclinicproxy.dto.DoctorDto;
import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.dto.PatientDto;
import com.example.medicalclinicproxy.exceptions.DoctorNotFoundException;
import com.example.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import com.example.medicalclinicproxy.exceptions.PatientNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.example.medicalclinicproxy.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MedicalClinicFacadeTest {
    @Autowired
    MedicalClinicFacade facade;
    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;
    @MockitoBean
    MedicalClinicClient clinicClient;

    @BeforeEach
    void resetCircuitBreakers() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(CircuitBreaker::reset);
    }

    @Test
    void getDoctor_WhenClinicResponds_ShouldReturnDoctorDto() {
        //Given
        DoctorDto expected = TestDataFactory.doctor();
        when(clinicClient.getDoctorById(DOCTOR_ID)).thenReturn(expected);
        //When
        DoctorDto result = facade.getDoctor(DOCTOR_ID);
        //Then
        assertEquals(expected, result);
        verify(clinicClient).getDoctorById(DOCTOR_ID);

    }

    @Test
    void getDoctor_WhenDoctorNotFound_ShouldRethrowDoctorNotFoundExceptionFromFallback() {
        //Given
        when(clinicClient.getDoctorById(DOCTOR_ID)).thenThrow(new DoctorNotFoundException(DOCTOR_ID));
        //When + Then
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> facade.getDoctor(DOCTOR_ID));
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Doctor with id: " + DOCTOR_ID + " not found", exception.getMessage())
        );
    }

    @Test
    void getDoctor_WhenClinicFails_ShouldThrowMedicalClinicUnavailableExceptionFromFallback() {
        //Given
        when(clinicClient.getDoctorById(DOCTOR_ID))
                .thenThrow(new IllegalStateException("connection refused"));
        //When + Then
        MedicalClinicUnavailableException exception = assertThrows(MedicalClinicUnavailableException.class,
                () -> facade.getDoctor(DOCTOR_ID));
        assertAll(
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus()),
                () -> assertEquals("Can not access doctor details - service unavailable", exception.getMessage())
        );
        verify(clinicClient).getDoctorById(DOCTOR_ID);
    }

    @Test
    void getDoctor_WhenCircuitBreakerIsOpen_ShouldThrowMedicalClinicUnavailableExceptionWithoutCallingClient() {
        //Given
        circuitBreakerRegistry.circuitBreaker("medical-clinic").transitionToOpenState();
        //When + Then
        MedicalClinicUnavailableException exception = assertThrows(MedicalClinicUnavailableException.class,
                () -> facade.getDoctor(DOCTOR_ID));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        verifyNoInteractions(clinicClient);
    }

    @Test
    void getPatient_WhenClinicResponds_ShouldReturnPatientDto() {
        //Given
        PatientDto expected = TestDataFactory.patient();
        when(clinicClient.getPatientById(PATIENT_ID)).thenReturn(expected);
        //When
        PatientDto result = facade.getPatient(PATIENT_ID);
        //Then
        assertEquals(expected, result);
        verify(clinicClient).getPatientById(PATIENT_ID);
    }

    @Test
    void getPatient_WhenPatientNotFound_ShouldRethrowPatientNotFoundExceptionFromFallback() {
        //Given
        when(clinicClient.getPatientById(PATIENT_ID)).thenThrow(new PatientNotFoundException(PATIENT_ID));
        //When + Then
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> facade.getPatient(PATIENT_ID));
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Patient with id: " + PATIENT_ID + " not found", exception.getMessage())
        );
        verify(clinicClient).getPatientById(PATIENT_ID);
    }

    @Test
    void getPatient_WhenClinicFails_ShouldThrowMedicalClinicUnavailableExceptionFromFallback() {
        //Given
        when(clinicClient.getPatientById(PATIENT_ID)).thenThrow(new IllegalStateException("Connection refused"));
        //When + Then
        MedicalClinicUnavailableException exception = assertThrows(MedicalClinicUnavailableException.class,
                () -> facade.getPatient(PATIENT_ID));
        assertAll(
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus()),
                () -> assertEquals("Can not access patient details - service unavailable", exception.getMessage())
        );
        verify(clinicClient).getPatientById(PATIENT_ID);
    }

    @Test
    void getPatient_WhenCircuitBreakerIsOpen_ShouldThrowMedicalClinicUnavailableExceptionWithoutCallingClient() {
        //Given
        circuitBreakerRegistry.circuitBreaker("medical-clinic").transitionToOpenState();
        //When + Then
        MedicalClinicUnavailableException exception = assertThrows(MedicalClinicUnavailableException.class,
                () -> facade.getPatient(PATIENT_ID));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        verifyNoInteractions(clinicClient);
    }

    @Test
    void getDoctorsFromSpecificSpeciality_WhenClinicResponds_ShouldReturnPageOfDoctorSummaries() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        PageDto<DoctorSummaryDto> expected = new PageDto<>(List.of(TestDataFactory.doctorSummary()), 0, 20, 1, 1);
        when(clinicClient.getDoctorsBySpeciality(CARDIOLOGY, pageable)).thenReturn(expected);
        //When
        PageDto<DoctorSummaryDto> result = facade.getDoctorsFromSpecificSpeciality(CARDIOLOGY, pageable);
        //Then
        assertEquals(expected, result);
        verify(clinicClient).getDoctorsBySpeciality(CARDIOLOGY, pageable);
    }

    @Test
    void getDoctorsFromSpecificSpeciality_WhenClinicFails_ShouldThrowMedicalClinicUnavailableExceptionFromFallback() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        when(clinicClient.getDoctorsBySpeciality(CARDIOLOGY, pageable)).thenThrow(new IllegalStateException("Connection refused"));
        //When + Then
        MedicalClinicUnavailableException exception = assertThrows(MedicalClinicUnavailableException.class,
                () -> facade.getDoctorsFromSpecificSpeciality(CARDIOLOGY, pageable));
        assertAll(
                () -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus()),
                () -> assertEquals("Can not get doctor details - service unavailable", exception.getMessage())
        );
        verify(clinicClient).getDoctorsBySpeciality(CARDIOLOGY, pageable);
    }

    @Test
    void getDoctorsFromSpecificSpeciality_WhenCircuitBreakerIsOpen_ShouldLogAndThrowMedicalClinicUnavailableException() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        circuitBreakerRegistry.circuitBreaker("medical-clinic").transitionToOpenState();
        //When + Then
        MedicalClinicUnavailableException exception = assertThrows(MedicalClinicUnavailableException.class,
                () -> facade.getDoctorsFromSpecificSpeciality(CARDIOLOGY, pageable));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        verifyNoInteractions(clinicClient);
    }


    @Test
    void circuitBreaker_WhenFailureRateThresholdExceeded_ShouldTransitionToOpen() {
        //Given
        CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("medical-clinic");
        when(clinicClient.getDoctorById(DOCTOR_ID)).thenThrow(new IllegalStateException("clinic down"));
        //When
        for (int i = 0; i < 10; i++) {
            assertThrows(MedicalClinicUnavailableException.class, () -> facade.getDoctor(DOCTOR_ID));
        }
        //Then
        assertEquals(CircuitBreaker.State.OPEN, breaker.getState());
    }

    @Test
    void circuitBreaker_WhenIgnoredExceptionIsThrown_ShouldNotCountAsFailure() {
        //Given
        CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("medical-clinic");
        when(clinicClient.getDoctorById(DOCTOR_ID)).thenThrow(new DoctorNotFoundException(DOCTOR_ID));
        //When
        assertThrows(DoctorNotFoundException.class, () -> facade.getDoctor(DOCTOR_ID));
        //Then
        assertAll(
                () -> assertEquals(0, breaker.getMetrics().getNumberOfBufferedCalls()),
                () -> assertEquals(0, breaker.getMetrics().getNumberOfFailedCalls()),
                () -> assertEquals(CircuitBreaker.State.CLOSED, breaker.getState())
        );
    }

    @Test
    void circuitBreaker_WhenNotIgnoredExceptionIsThrown_ShouldCountAsFailure() {
        //Given
        CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("medical-clinic");
        when(clinicClient.getDoctorById(DOCTOR_ID)).thenThrow(new IllegalStateException("clinic down"));
        //When
        assertThrows(MedicalClinicUnavailableException.class, () -> facade.getDoctor(DOCTOR_ID));
        //Then
        assertEquals(1, breaker.getMetrics().getNumberOfFailedCalls());
    }
}
