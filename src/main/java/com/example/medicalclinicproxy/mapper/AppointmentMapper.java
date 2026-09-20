package com.example.medicalclinicproxy.mapper;

import com.example.medicalclinicproxy.dto.AppointmentDto;
import com.example.medicalclinicproxy.dto.AvailableAppointmentSummary;
import com.example.medicalclinicproxy.dto.CreateAppointmentCommand;
import com.example.medicalclinicproxy.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctorName", ignore = true)
    @Mapping(target = "patientName", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    Appointment toEntity(CreateAppointmentCommand command);

    AvailableAppointmentSummary toAvailableAppointment(Appointment appointment);

    AppointmentDto toDto(Appointment appointment);
}
