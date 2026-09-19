package com.example.medicalclinicproxy.facade;

import com.example.medicalclinicproxy.client.MedicalClinicClient;
import com.example.medicalclinicproxy.dto.DoctorDto;
import com.example.medicalclinicproxy.dto.PatientDto;
import com.example.medicalclinicproxy.exceptions.DoctorNotFoundException;
import com.example.medicalclinicproxy.exceptions.MedicalClinicUnavailableException;
import com.example.medicalclinicproxy.exceptions.PatientNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MedicalClinicFacade {
    private final MedicalClinicClient clinicClient;
    @CircuitBreaker(name = "medical-clinic",fallbackMethod = "getDoctorFallback")
    public DoctorDto getDoctor(@NonNull Long id){
            return clinicClient.getDoctorById(id);
    }
    @CircuitBreaker(name = "medical-clinic",fallbackMethod = "")
    public PatientDto getPatient(@NonNull Long id){return clinicClient.getPatientById(id);}

    private DoctorDto getDoctorFallback(Long id, Throwable throwable){
        if(throwable instanceof DoctorNotFoundException exception){
            throw exception;
        }
        log.error("There was a problem where calling medical-clinic, doctor id {}",id,throwable);
        throw new MedicalClinicUnavailableException("Can not access doctor details - service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }
    private PatientDto getPatientFallback(Long id, Throwable throwable){
        if(throwable instanceof PatientNotFoundException exception){
            throw exception;
        }
        log.error("There was a problem where calling medical-clinic, patient id {}",id,throwable);
        throw new MedicalClinicUnavailableException("Can not access patient details - service unavailable", HttpStatus.SERVICE_UNAVAILABLE);

    }
}
