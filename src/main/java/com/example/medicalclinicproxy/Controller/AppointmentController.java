package com.example.medicalclinicproxy.Controller;

import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.searchCriteria.AppointmentSearchCriteria;
import com.example.medicalclinicproxy.searchCriteria.AvailableAppointmentCriteria;
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

    @Operation(summary = "Search appointments by patient, doctor, specialization, date range or timeframe")
    @ApiResponse(responseCode = "200", description = "Appointments found")
    @ApiResponse(responseCode = "400", description = "Invalid filter values")
    @GetMapping
    public PageDto<AppointmentDto> search(@ParameterObject AppointmentSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.search(criteria, pageable);
    }

    @Operation(summary = "Search available appointment slots (no patient assigned)")
    @ApiResponse(responseCode = "200", description = "Available slots found")
    @GetMapping("/available")
    public PageDto<AvailableAppointmentSummary> searchAvailable(
            @ParameterObject AvailableAppointmentCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable){
        return service.searchAvailable(criteria,pageable);
    }

    @Operation(summary = "Get appointment by id")
    @ApiResponse(description = "appointment found", responseCode = "200")
    @ApiResponse(description = "Appointment not found", responseCode = "404")
    @GetMapping("/{id}")
    public AppointmentDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Create appointment, patient does not need to be assigned")
    @ApiResponse(responseCode = "201", description = "Appointment created")
    @ApiResponse(responseCode = "400", description = "Appointment is in the past or does not start at a full quarter of an hour")
    @ApiResponse(responseCode = "404", description = "Doctor not found")
    @ApiResponse(responseCode = "409", description = "Appointment overlaps with another appointment of this doctor")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
