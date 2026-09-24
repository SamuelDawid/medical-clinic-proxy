package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

class DoctorServiceTest {

    private MedicalClinicFacade facade;
    private DoctorService service;

    @BeforeEach
    void setUp() {
        this.facade = Mockito.mock(MedicalClinicFacade.class);
        this.service = new DoctorService(facade);
    }

}
