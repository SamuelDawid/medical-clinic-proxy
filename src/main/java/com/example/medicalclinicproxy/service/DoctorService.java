package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorService {
    MedicalClinicFacade facade;

    @Transactional(readOnly = true)
    public PageDto<DoctorSummaryDto> findBySpeciality(@NotBlank String speciality, Pageable pageable) {
        return facade.getDoctorsFromSpecificSpeciality(speciality, pageable);
    }
}
