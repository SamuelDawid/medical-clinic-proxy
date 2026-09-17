package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.client.MedicalClinicClient;
import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.exceptions.AppointmentDoesNotExistsException;
import com.example.medicalclinicproxy.exceptions.InvalidDateOfAppointmentException;
import com.example.medicalclinicproxy.exceptions.InvalidTimeOfTheAppointmentException;
import com.example.medicalclinicproxy.exceptions.TimeIsOverlappingWithAnotherAppointmentException;
import com.example.medicalclinicproxy.mapper.AppointmentMapper;
import com.example.medicalclinicproxy.model.Appointment;
import com.example.medicalclinicproxy.repository.AppointmentRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AppointmentService {
    MedicalClinicClient clinicClient;
    AppointmentMapper mapper;
    AppointmentRepository repository;

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

    @Transactional
    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        log.info("Creating appointment with doctor Id {} with date {} to {}",
                command.doctorId(), command.startDateTime(), command.endDateTime());
        Appointment appointment = mapper.toEntity(command);
        DoctorDto doctorById = clinicClient.getDoctorById(command.doctorId());
        appointment.setDoctorId(doctorById.id());

        validateDate(appointment.getStartDateTime());
        validateTimeOfTheVisit(appointment);
        Appointment saved = repository.save(appointment);
        log.info("Appointment with Id {} Created", appointment.getId());
        return mapper.toDto(saved);
    }

    private void validateDate(@NonNull LocalDateTime dateAndTime) {
        log.debug("Checking if time is correct {}", dateAndTime);
        if (dateAndTime.isBefore(LocalDateTime.now())) {
            log.warn("Invalid date, must be ahead of {}", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            throw new InvalidDateOfAppointmentException();
        }
    }

    private void validateTimeOfTheVisit(@NonNull Appointment appointmentToCreate) {
        log.debug("Checking overlaps for doctor {} between {} and {}",
                appointmentToCreate.getDoctorId(), appointmentToCreate.getStartDateTime(), appointmentToCreate.getEndDateTime());
        int minutes = appointmentToCreate.getStartDateTime().getMinute();
        if (!validateMinutes(minutes)) {
            throw new InvalidTimeOfTheAppointmentException();
        }
        Set<Appointment> appointments = repository.findByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                appointmentToCreate.getDoctorId(),
                appointmentToCreate.getEndDateTime(),
                appointmentToCreate.getStartDateTime()
        );
        log.debug("Found {} overlapping appointments", appointments.size());
        if (!appointments.isEmpty()) {
            throw new TimeIsOverlappingWithAnotherAppointmentException();
        }
    }

    private Appointment findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Appointment with id {} does not exists", id);
                    return new AppointmentDoesNotExistsException();
                });
    }

    private boolean validateMinutes(int minutes) {
        return minutes % 15 == 0;
    }
}
