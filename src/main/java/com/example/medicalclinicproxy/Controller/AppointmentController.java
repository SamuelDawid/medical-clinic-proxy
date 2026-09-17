package com.example.medicalclinicproxy.Controller;

import com.example.medicalclinicproxy.dto.AppointmentDto;
import com.example.medicalclinicproxy.dto.CreateAppointmentCommand;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@AllArgsConstructor
public class AppointmentController {
    private final AppointmentService service;

    @GetMapping("/patient/{id}")
    public PageDto<AppointmentDto> getAllForPatient(@PathVariable("id") Long id, Pageable pageable) {
        return service.findAllByPatientId(id, pageable);
    }
    @GetMapping("/doctor/{id}")
    public PageDto<AppointmentDto> getAllForDoctor (@PathVariable("id") Long id, Pageable pageable) {
        return service.findAllByPatientId(id, pageable);
    }
    @PostMapping
    public AppointmentDto create(@RequestBody CreateAppointmentCommand command) {
        return service.create(command);
    }
}
