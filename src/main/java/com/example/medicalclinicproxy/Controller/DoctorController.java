package com.example.medicalclinicproxy.Controller;

import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.searchCriteria.DoctorSearchCriteria;
import com.example.medicalclinicproxy.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Doctors", description = "Doctor data from medical-clinic service")
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;

    @Operation(summary = "Search doctors by speciality")
    @ApiResponse(responseCode = "200", description = "Doctors found (empty page if none)")
    @ApiResponse(responseCode = "503", description = "medical-clinic service unavailable")
    @GetMapping
    public PageDto<DoctorSummaryDto> bySpecialty(@RequestParam String speciality,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return service.search(speciality, pageable);
    }
}
