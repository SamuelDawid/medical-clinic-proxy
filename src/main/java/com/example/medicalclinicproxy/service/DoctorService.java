package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorService {
    private final MedicalClinicFacade facade;

    public PageDto<DoctorSummaryDto> search(@NonNull String speciality, Pageable pageable) {
        log.info("Searching doctors with speciality {}", speciality);
        return facade.getDoctorsFromSpecificSpeciality(speciality, pageable);
    }
}
