package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.exceptions.*;
import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import com.example.medicalclinicproxy.mapper.AppointmentMapper;
import com.example.medicalclinicproxy.model.Appointment;
import com.example.medicalclinicproxy.repository.AppointmentRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AppointmentService {
    private final MedicalClinicFacade facade;
    private final AppointmentMapper mapper;
    private final AppointmentRepository repository;

    @Transactional(readOnly = true)
    public PageDto<AppointmentDto> findAll(Pageable pageable) {
        return PageDto.from(repository.findAll(pageable).map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public PageDto<AppointmentDto> findAllByPatientId(@NonNull Long id, Pageable pageable) {
        return PageDto.from(repository.findAllByPatientId(id, pageable).map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public PageDto<AppointmentDto> findAllByDoctorId(@NonNull Long id, Pageable pageable) {
        return PageDto.from(repository.findAllByDoctorId(id, pageable).map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public AppointmentDto findById(@NonNull Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageDto<AvailableAppointmentSummary> findAvailableAppointmentsForSpecialization(FindFreeAppointmentsBySpecializationAndDateCommand command, Pageable pageable) {
        return PageDto.from(repository.findAvailableSlotsBySpecialization(command.specialization(),command.startDate(),command.endDate(), pageable)
                .map(mapper::toAvailableAppointment));
    }

    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        log.info("Creating appointment with doctor Id {} with date {} to {}",
                command.doctorId(), command.startDateTime(), command.endDateTime());
        validateDate(command.startDateTime());
        validateTimeOfTheVisit(command.startDateTime(), command.endDateTime());
        validateSlotIsFree(command.startDateTime(), command.endDateTime(), command.doctorId());
        DoctorDto doctor = facade.getDoctor(command.doctorId());
        Appointment appointment = mapper.toEntity(command);
        appointment.setDoctorName(appointment.fullName(doctor.userDto()));
        appointment.setDoctorSpecialisation(doctor.medicalSpecialty());
        Appointment saved = repository.save(appointment);
        log.info("Appointment with Id {} Created", saved.getId());
        return mapper.toDto(saved);
    }

    @Transactional
    public void removePatientFromVisit(@NonNull Long appointmentId) {
        log.info("Removing Patient from Visit with Id {}", appointmentId);
        Appointment appointment = findOrThrow(appointmentId);
        appointment.setPatientId(null);
        appointment.setPatientName(null);
        log.info("Patient removed successfully from visit with Id {} ", appointmentId);
    }

    @Transactional
    public AppointmentDto assignPatientToAppointment(@NonNull AssignPatientToAppointmentCommand command) {
        log.info("Assigning Patient with Id {} To Appointment with Id {}", command.patientId(), command.appointmentId());
        Appointment appointment = repository.findWithLockById(command.appointmentId())
                .orElseThrow(AppointmentDoesNotExistsException::new);
        if (appointment.getPatientId() != null) {
            log.warn("Couldn't assign patient because appointment already exists");
            throw new AppointmentAlreadyTakenException();
        }
        PatientDto patientDto = facade.getPatient(command.patientId());
        appointment.setPatientId(patientDto.id());
        appointment.setPatientName(appointment.fullName(patientDto.userDto()));
        log.info("Patient with Id {} assigned successfully to visit with Id {}", command.patientId(), command.appointmentId());
        return mapper.toDto(appointment);
    }

    @Transactional
    public void delete(@NonNull Long appointmentId) {
        log.info("Deleting appointment with id {}", appointmentId);
        Appointment appointment = findOrThrow(appointmentId);
        repository.delete(appointment);
        log.info("Appointment {} deleted successfully", appointmentId);
    }

    private void validateDate(@NonNull LocalDateTime dateAndTime) {
        log.debug("Checking if time is correct {}", dateAndTime);
        if (dateAndTime.isBefore(LocalDateTime.now())) {
            log.error("Invalid date, must be ahead of {}", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            throw new InvalidDateOfAppointmentException();
        }
    }

    private void validateTimeOfTheVisit(@NonNull LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Validating time of visit {} -> {}",
                startTime, endTime);
        int minutesStart = startTime.getMinute();
        int minutesEnd = endTime.getMinute();
        if (!validateMinutes(minutesStart) || !validateMinutes(minutesEnd)) {
            throw new InvalidTimeOfTheAppointmentException();
        }
    }

    private void validateSlotIsFree(@NonNull LocalDateTime startTime, LocalDateTime endTime, Long doctorId) {
        log.debug("Checking overlaps for doctor {} between {} and {}",
                doctorId, startTime, endTime);
        Set<Appointment> appointments = repository.findByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                doctorId,
                endTime,
                startTime
        );
        log.debug("Found {} overlapping appointments", appointments.size());
        if (!appointments.isEmpty()) {
            throw new TimeIsOverlappingWithAnotherAppointmentException();
        }
    }

    private Appointment findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Appointment with id {} does not exists", id);
                    return new AppointmentDoesNotExistsException();
                });
    }

    private boolean validateMinutes(int minutes) {
        return minutes % 15 == 0;
    }
}
