package com.example.medicalclinicproxy.facade;

import com.example.medicalclinicproxy.client.MedicalClinicClient;
import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.exceptions.DoctorNotFoundException;
import com.example.medicalclinicproxy.exceptions.MedicalClinicProxyException;
import com.example.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import com.example.medicalclinicproxy.exceptions.PatientNotFoundException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MedicalClinicFacade {
    private final MedicalClinicClient clinicClient;

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "getDoctorFallback")
    public DoctorDto getDoctor(@NonNull Long id) {
        return clinicClient.getDoctorById(id);
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "getPatientFallback")
    public PatientDto getPatient(@NonNull Long id) {
        return clinicClient.getPatientById(id);
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "getDoctorsFromSpecificSpecialityFallback")
    public PageDto<DoctorSummaryDto> getDoctorsFromSpecificSpeciality(String speciality, Pageable pageable) {
        return clinicClient.getDoctorsBySpeciality(speciality, pageable.getPageNumber(), pageable.getPageSize());
    }

    @CircuitBreaker(name = "medical-clinic")
    public PageDto<AppointmentDto> searchAppointments(AppointmentSearchCriteria criteria, Pageable pageable) {
        return clinicClient.searchAppointments(criteria.patientId(), criteria.doctorId(), criteria.specialization(), criteria.from(), criteria.to(), criteria.timeframe(), pageable.getPageNumber(), pageable.getPageSize());
    }

    @CircuitBreaker(name = "medical-clinic")
    public AppointmentDto findById(@NonNull Long id) {
        return clinicClient.findById(id);
    }

    @CircuitBreaker(name = "medical-clinic")
    public PageDto<AvailableAppointmentSummary> searchAvailable(AvailableAppointmentCriteria criteria, Pageable pageable) {
        return clinicClient.searchAvailable(criteria.doctorId(), criteria.specialization(), criteria.from(), criteria.to(), pageable.getPageNumber(), pageable.getPageSize());
    }

    @CircuitBreaker(name = "medical-clinic")
    public void patientCancelAppointment(@NonNull Long id) {
        clinicClient.patientCancelAppointment(id);
    }

    @CircuitBreaker(name = "medical-clinic")
    public void deleteAppointment(@NotNull Long id) {
        clinicClient.deleteAppointment(id);
    }

    @CircuitBreaker(name = "medical-clinic")
    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        return clinicClient.create(command);
    }

    @CircuitBreaker(name = "medical-clinic")
    public AppointmentDto assignPatient(@NonNull AssignPatientToAppointmentCommand command) {
        return clinicClient.assignPatient(command);
    }

    private PageDto<DoctorSummaryDto> getDoctorsFromSpecificSpecialityFallback(String speciality, Pageable pageable, Throwable throwable) {
        if (throwable instanceof MedicalClinicProxyException exception) {
            throw exception;
        }
        if (throwable instanceof CallNotPermittedException) {
            log.error("Circuit breaker OPEN, speciality {}", speciality);
        } else {
            log.error("Error getting doctor details, speciality {}", speciality, throwable);
        }
        throw new MedicalClinicUnavailableException("Can not get doctor details - service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }

    private DoctorDto getDoctorFallback(Long id, Throwable throwable) {
        if (throwable instanceof DoctorNotFoundException exception) {
            throw exception;
        }
        log.error("There was a problem where calling medical-clinic, doctor id {}", id, throwable);
        throw new MedicalClinicUnavailableException("Can not access doctor details - service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }

    private PatientDto getPatientFallback(Long id, Throwable throwable) {
        if (throwable instanceof PatientNotFoundException exception) {
            throw exception;
        }
        log.error("There was a problem where calling medical-clinic, patient id {}", id, throwable);
        throw new MedicalClinicUnavailableException("Can not access patient details - service unavailable", HttpStatus.SERVICE_UNAVAILABLE);

    }
}
