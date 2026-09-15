package com.example.medicalclinicproxy.mapper;

import com.example.medicalclinicproxy.dto.CreateAppointmentCommand;
import com.example.medicalclinicproxy.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    Appointment toEntity(CreateAppointmentCommand command);
    @Mapping(target = "doctorName", source = "doctor.user")
    @Mapping(target = "patientName", source = "patient.user")
    AppointmentDto toDto(Appointment appointment);
    default String fullName(User user){
        if(user == null){ return  null;}
        return user.getFirstName() + " " + user.getLastName();
    }
}
