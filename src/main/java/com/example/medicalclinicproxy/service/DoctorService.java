package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import com.example.medicalclinicproxy.searchCriteria.DoctorSearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorService {
    private final MedicalClinicFacade facade;

    public PageDto<DoctorSummaryDto> search(DoctorSearchCriteria criteria, Pageable pageable) {
        log.info("Searching doctors with speciality {}", criteria.speciality());
        return facade.getDoctorsFromSpecificSpeciality(criteria.speciality(), pageable);
    }
}
