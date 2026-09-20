package com.example.medicalclinicproxy.client;

import com.example.medicalclinicproxy.dto.DoctorDto;
import com.example.medicalclinicproxy.dto.PatientDto;
import com.example.medicalclinicproxy.medicalClinicFeignClientConfig.FeignMedicalClinicConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "medical-clinic", url = "${medical-clinic.api.url}", configuration = FeignMedicalClinicConfiguration.class)
public interface MedicalClinicClient {

    @GetMapping("/patients/{id}")
    PatientDto getPatientById(@PathVariable Long id);

    @GetMapping("/doctors/{id}")
    DoctorDto getDoctorById(@PathVariable Long id);
}
