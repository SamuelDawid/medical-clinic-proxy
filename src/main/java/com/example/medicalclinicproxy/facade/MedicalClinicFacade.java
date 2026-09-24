package com.example.medicalclinicproxy.facade;

import com.example.medicalclinicproxy.client.MedicalClinicClient;
import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.exceptions.MedicalClinicProxyException;
import com.example.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
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

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "getDoctorsFromSpecificSpecialityFallback")
    public PageDto<DoctorSummaryDto> getDoctorsFromSpecificSpeciality(String speciality, Pageable pageable) {
        return clinicClient.getDoctorsBySpeciality(speciality, pageable.getPageNumber(), pageable.getPageSize());
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "searchAppointmentsFallback")
    public PageDto<AppointmentDto> searchAppointments(AppointmentSearchCriteria criteria, Pageable pageable) {
        return clinicClient.searchAppointments(criteria.patientId(), criteria.doctorId(), criteria.specialization(), criteria.from(), criteria.to(), criteria.timeframe(), pageable.getPageNumber(), pageable.getPageSize());
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "findByIdFallback")
    public AppointmentDto findById(@NonNull Long id) {
        return clinicClient.findById(id);
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "searchAvailableFallback")
    public PageDto<AvailableAppointmentSummary> searchAvailable(AvailableAppointmentCriteria criteria, Pageable pageable) {
        return clinicClient.searchAvailable(criteria.doctorId(), criteria.specialization(), criteria.from(), criteria.to(), pageable.getPageNumber(), pageable.getPageSize());
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "patientCancelAppointmentFallback")
    public void patientCancelAppointment(@NonNull Long id) {
        clinicClient.patientCancelAppointment(id);
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "deleteAppointmentFallback")
    public void deleteAppointment(@NotNull Long id) {
        clinicClient.deleteAppointment(id);
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "createFallback")
    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        return clinicClient.create(command);
    }

    @CircuitBreaker(name = "medical-clinic", fallbackMethod = "assignPatientFallback")
    public AppointmentDto assignPatient(@NonNull AssignPatientToAppointmentCommand command) {
        return clinicClient.assignPatient(command);
    }

    private PageDto<DoctorSummaryDto> getDoctorsFromSpecificSpecialityFallback(String speciality, Pageable pageable, Throwable throwable) {
        throw toProxyException("getDoctorsBySpeciality " + speciality, throwable);
    }

    private PageDto<AppointmentDto> searchAppointmentsFallback(AppointmentSearchCriteria criteria, Pageable pageable, Throwable throwable) {
        throw toProxyException("searchAppointments " + criteria, throwable);
    }

    private AppointmentDto findByIdFallback(Long id, Throwable throwable) {
        throw toProxyException("findAppointment id=" + id, throwable);
    }

    private PageDto<AvailableAppointmentSummary> searchAvailableFallback(AvailableAppointmentCriteria criteria, Pageable pageable, Throwable throwable) {
        throw toProxyException("searchAvailable " + criteria, throwable);
    }

    private void patientCancelAppointmentFallback(Long id, Throwable throwable) {
        throw toProxyException("cancelAppointment id=" + id, throwable);
    }

    private void deleteAppointmentFallback(Long id, Throwable throwable) {
        throw toProxyException("deleteAppointment id=" + id, throwable);
    }

    private AppointmentDto createFallback(CreateAppointmentCommand command, Throwable throwable) {
        throw toProxyException("createAppointment doctorId=" + command.doctorId(), throwable);
    }

    private AppointmentDto assignPatientFallback(AssignPatientToAppointmentCommand command, Throwable throwable) {
        throw toProxyException("assignPatient appointmentId=" + command.appointmentId(), throwable);
    }

    private RuntimeException toProxyException(String operation, Throwable throwable) {
        if (throwable instanceof MedicalClinicProxyException exception) {
            return exception;
        }
        if (throwable instanceof CallNotPermittedException) {
            log.error("Circuit breaker OPEN – {} rejected without calling medical-clinic", operation);
        } else {
            log.error("medical-clinic unavailable during {}", operation, throwable);
        }
        return new MedicalClinicUnavailableException(
                "medical-clinic is unavailable, try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
