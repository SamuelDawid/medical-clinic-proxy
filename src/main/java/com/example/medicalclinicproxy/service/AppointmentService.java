package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AppointmentService {
    private final MedicalClinicFacade facade;

    public AppointmentDto findById(@NonNull Long id) {
        return facade.findById(id);
    }

    public PageDto<AvailableAppointmentSummary> searchAvailable(AvailableAppointmentCriteria criteria, Pageable pageable) {
        log.info("searching for available appointments with criteria {}", criteria);
        return facade.searchAvailable(criteria, pageable);
    }

    public PageDto<AppointmentDto> search(AppointmentSearchCriteria criteria, Pageable pageable) {
        log.info("searching for appointments with criteria {}", criteria);
        return facade.searchAppointments(criteria, pageable);
    }

    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        log.info("Creating new appointment -> {}", command);
        AppointmentDto result = facade.create(command);
        log.info("appointment {} created successfully {}", result.id(), result);
        return result;
    }

    public void removePatientFromVisit(@NonNull Long appointmentId) {
        log.info("Removing Patient from Visit with Id {}", appointmentId);
        facade.patientCancelAppointment(appointmentId);
        log.info("Patient removed from Visit with Id {}", appointmentId);
    }

    public AppointmentDto assignPatientToAppointment(@NonNull AssignPatientToAppointmentCommand command) {
        log.info("Assigning Patient with Id {} To Appointment with Id {}", command.patientId(), command.appointmentId());
        AppointmentDto result = facade.assignPatient(command);
        log.info("Patient with Id {} assigned successfully to Appointment with Id {}", command.patientId(), command.appointmentId());
        return result;
    }

    public void delete(@NonNull Long appointmentId) {
        log.info("Deleting appointment with id {}", appointmentId);
        facade.deleteAppointment(appointmentId);
        log.info("Appointment {} deleted successfully", appointmentId);
    }

}
