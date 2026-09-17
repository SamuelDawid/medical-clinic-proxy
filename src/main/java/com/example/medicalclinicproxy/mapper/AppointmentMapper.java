package com.example.medicalclinicproxy.mapper;

import com.example.medicalclinicproxy.dto.AppointmentDto;
import com.example.medicalclinicproxy.dto.CreateAppointmentCommand;
import com.example.medicalclinicproxy.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "id",ignore = true)
    Appointment toEntity(CreateAppointmentCommand command);

    AppointmentDto toDto(Appointment appointment);
}
