package com.example.medicalclinicproxy.Controller;

import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.service.AppointmentService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Appointments", description = "Operations for managing Appointments records")
@RestController
@RequestMapping("/appointments")
@AllArgsConstructor
public class AppointmentController {
    private final AppointmentService service;

    @Operation(summary = "Get all appointments")
    @ApiResponse(description = "Get all appointments", responseCode = "200")
    @GetMapping
    public PageDto<AppointmentDto> findAll(@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.findAll(pageable);
    }
    @Operation(summary = "Get all appointments")
    @ApiResponse(description = "Get all appointments", responseCode = "200")
    @GetMapping("/available")
    public PageDto<AvailableAppointmentSummary> findAvailableAppointmentsBySpecialization(@RequestBody FindFreeAppointmentsBySpecializationAndDateCommand command,@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable){
        return service.findAvailableAppointmentsForSpecialization(command, pageable);
    }
    @Operation(summary = "Get appointments by Patient id")
    @ApiResponse(description = "appointments found", responseCode = "200")
    @GetMapping("/patient/{id}")
    public PageDto<AppointmentDto> getAllForPatient(@PathVariable("id") Long id, Pageable pageable) {
        return service.findAllByPatientId(id, pageable);
    }

    @Operation(summary = "Get appointments by Doctor id")
    @ApiResponse(description = "appointments found", responseCode = "200")
    @GetMapping("/doctor/{id}")
    public PageDto<AppointmentDto> getAllForDoctor(@PathVariable("id") Long id, Pageable pageable) {
        return service.findAllByPatientId(id, pageable);
    }

    @Operation(summary = "Get appointment by id")
    @ApiResponse(description = "appointment found", responseCode = "200")
    @ApiResponse(description = "Appointment not found", responseCode = "404")
    @GetMapping("{id}")
    public AppointmentDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Create appointment, patient does not need to be assigned")
    @ApiResponse(responseCode = "201", description = "Appointment created")
    @ApiResponse(responseCode = "400", description = "Appointment is in the past or does not start at a full quarter of an hour")
    @ApiResponse(responseCode = "404", description = "Doctor not found")
    @ApiResponse(responseCode = "409", description = "Appointment overlaps with another appointment of this doctor")
    @PostMapping
    public AppointmentDto create(@RequestBody CreateAppointmentCommand command) {
        return service.create(command);
    }

    @Operation(summary = "Assign patient to appointment")
    @ApiResponse(responseCode = "200", description = "Patient assigned successfully")
    @ApiResponse(responseCode = "400", description = "Appointment is already in the past")
    @ApiResponse(responseCode = "404", description = "Appointment or patient not found")
    @ApiResponse(responseCode = "409", description = "Appointment is already taken")
    @PutMapping()
    public AppointmentDto assignPatientToAppointment(@RequestBody AssignPatientToAppointmentCommand command) {
        return service.assignPatientToAppointment(command);
    }

    @Operation(summary = "Remove Patient from Appointment")
    @ApiResponse(description = "Appointment not found", responseCode = "404")
    @ApiResponse(description = "Patient removed successfully", responseCode = "204")
    @PatchMapping("/cancel/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patientAppointmentCancel(@PathVariable Long appointmentId) {
        service.removePatientFromVisit(appointmentId);
    }

    @Operation(summary = "Delete Appointment")
    @ApiResponse(description = "Appointment deleted successfully", responseCode = "204")
    @ApiResponse(description = "Appointment not found", responseCode = "404")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
