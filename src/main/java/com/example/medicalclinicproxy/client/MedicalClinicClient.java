package com.example.medicalclinicproxy.client;

import com.example.medicalclinicproxy.dto.*;
import com.example.medicalclinicproxy.enums.Timeframe;
import com.example.medicalclinicproxy.medicalClinicFeignClientConfig.FeignMedicalClinicConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@FeignClient(value = "medical-clinic", url = "${medical-clinic.api.url}", configuration = FeignMedicalClinicConfiguration.class)
public interface MedicalClinicClient {

    @GetMapping("/patients/{id}")
    PatientDto getPatientById(@PathVariable Long id);

    @GetMapping("/doctors/{id}")
    DoctorDto getDoctorById(@PathVariable Long id);

    @GetMapping("/doctors")
    PageDto<DoctorSummaryDto> getDoctorsBySpeciality(@RequestParam("specialty") String specialty,
            @RequestParam("page") int page,
            @RequestParam("size") int size);

    @GetMapping("/appointments")
    PageDto<AppointmentDto> searchAppointments(@RequestParam(value = "patientId", required = false) Long patientId,
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "specialization", required = false) String specialization,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(value = "timeframe", required = false) Timeframe timeframe,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @GetMapping("/appointments/{id}")
    AppointmentDto findById(@PathVariable("id") Long id);

    @GetMapping("/appointments/available")
    PageDto<AvailableAppointmentSummary> searchAvailable(
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "specialization", required = false) String specialization,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @PutMapping("/appointments")
    AppointmentDto assignPatient(@RequestBody AssignPatientToAppointmentCommand command);

    @PatchMapping("/appointments/cancel/{appointmentId}")
    void patientCancelAppointment(@PathVariable("appointmentId") Long id);

    @DeleteMapping("/appointments/{id}")
    void deleteAppointment(@PathVariable("id") Long id);

    @PostMapping("/appointments")
    AppointmentDto create(@RequestBody CreateAppointmentCommand command);
}
